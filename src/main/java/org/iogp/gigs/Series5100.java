/*
 * GIGS - Geospatial Integrity of Geoscience Software
 * https://gigs.iogp.org/
 *
 * Copyright (C) 2022 International Association of Oil and Gas Producers.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package org.iogp.gigs;

import org.iogp.gigs.internal.geoapi.Configuration;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.operation.*;
import org.opengis.util.FactoryException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


/**
 * Base class for tests of conversions from geographic CRS to projected CRS and reverse (5100 series).
 * The test procedures in this series evaluate the software’s capabilities
 * to perform conversions (map projections).
 * The conversions are performed using {@link org.opengis.referencing.operation.CoordinateOperationFactory}.
 *
 * @author  Estelle Idée
 * @version 1.0
 * @since   1.0
 */
public abstract class Series5100 extends Series5000 {
    /**
     * Factory to use for building {@link Conversion} instances, or {@code null} if none.
     * This is the factory used by the {@link #convertPoint(double[])} ()} method.
     */
    protected final CoordinateOperationFactory copFactory;

    /**
     * Data about the base CRS of the geographic CRS.
     *
     * @see #createGeogCRS(TestMethod)
     */
    final Test3205 geogCRSTest;

    /**
     * Data about the CRS of the projected CRS.
     *
     * @see #createProjCRS(TestMethod)
     */
    final Test3207 projCRSTest;

    /**
     * The geographic CRS created by this factory.
     */
    protected GeographicCRS geogCRS;

    /**
     * The projected CRS created by the factory,
     * or {@code null} if not yet created or if projected CRS creation failed.
     */
    protected ProjectedCRS projCRS;

    /**
     * Creates a new test using the given factories.
     * The factories needed by this class are {@link CoordinateOperationFactory}.
     * If a requested factory is {@code null}, then the tests which depend on it will be skipped.
     *
     * <h4>Authority factory usage</h4>
     * The authority factory is used only for some test cases where the components are fetched by EPSG codes
     * instead of being built by user. Those test cases are identified by the "definition source" line in Javadoc.
     *
     * @param factories  Factories for creating the instances to test.
     * @param cartTolerance the Cartesian tolerance to use for the tests.
     * @param geoTolerance the Geographic tolerance to use for the tests.
     * @param rtCartTolerance the Round Trip Cartesian tolerance to use for the tests.
     * @param rtGeoTolerance the Round Trip Geographic tolerance to use for the tests.
     *
     * @throws FactoryException if an error occurred while creating the {@link CoordinateOperation}
     * and @{@link org.opengis.referencing.crs.CoordinateReferenceSystem}.
     */
    public Series5100(final Factories factories, final double cartTolerance, final double geoTolerance, final double rtCartTolerance, final double rtGeoTolerance) throws FactoryException {
        super(cartTolerance, geoTolerance, rtCartTolerance, rtGeoTolerance);
        copFactory            = factories.copFactory;

        geogCRSTest = new Test3205(factories);
        geogCRSTest.skipTests = true;
        geogCRSTest.skipIdentificationCheck = true;

        projCRSTest = new Test3207(factories);
        projCRSTest.skipTests = true;
        projCRSTest.skipIdentificationCheck = true;

        createCRSs();
        createCoOp();
    }

    /**
     * Create the {@link CoordinateOperation} for the forward and reverse tests.
     *
     * @throws FactoryException if an error occurred while creating the {@link CoordinateOperation}.
     */
    protected void createCoOp() throws FactoryException {
        forwardCoOp = copFactory.createOperation(geogCRS, projCRS);
        reverseCoOp = copFactory.createOperation(projCRS, geogCRS);
    }

    /**
     * Returns information about the configuration of the test which has been run.
     * This method returns a map containing:
     *
     * <ul>
     *   <li>All the following values associated to the {@link Configuration.Key} of the same name:
     *     <ul>
     *       <li>{@link #isFactoryPreservingUserValues}</li>
     *       <li>{@link #copFactory}</li>
     *     </ul>
     *   </li>
     * </ul>
     *
     * @return the configuration of the test being run.
     */
    @Override
    Configuration configuration() {
        final Configuration op = super.configuration();
        assertNull(op.put(Configuration.Key.copFactory,            copFactory));
        return op;
    }

    /**
     * Creates a user-defined base CRS by executing the specified method from the {@link Test3205} class.
     *
     * @param  factory          the test method to use for creating the geographic CRS.
     * @throws FactoryException if an error occurred while creating the geographic CRS.
     */
    void createGeogCRS(final TestMethod<Test3205> factory) throws FactoryException {
        factory.initialize(geogCRSTest);
        geogCRS = (GeographicCRS) geogCRSTest.getIdentifiedObject();
    }

    /**
     * Creates a user-defined projected CRS by executing the specified method from the {@link Test3207} class.
     *
     * @param  factory          the test method to use for creating the projected CRS.
     * @throws FactoryException if an error occurred while creating the projected CRS.
     */
    void createProjCRS(final TestMethod<Test3207> factory) throws FactoryException {
        factory.initialize(projCRSTest);
        projCRS = projCRSTest.getIdentifiedObject();
    }

    /**
     * Verifies the result of the conversion produced by {@link #convertPoint(double[])} ()}.
     *
     * @param expectedPoint the expected coordinates from the GIGS output file.
     * @param resPoint the coordinates computed by the factory.
     */
    @Override
    void verifyConversion(double[] expectedPoint, double[] resPoint) {
        final double tolerance;
        if (isForward) {
            tolerance = isRTC ? rtCartTolerance : cartTolerance;
        } else {
            tolerance = isRTC ? rtGeoTolerance : geoTolerance;
        }
        assertArrayEquals(expectedPoint, resPoint, tolerance);
    }
}
