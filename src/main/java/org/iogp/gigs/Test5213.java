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

import org.junit.jupiter.api.DisplayName;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.cs.CSFactory;
import org.opengis.referencing.datum.DatumFactory;
import org.opengis.referencing.operation.*;
import org.opengis.util.FactoryException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;


/**
 * Verifies the software’s capabilities to perform coordinate transformations for the geocentric translation method which operate
 * ‘from’ and ‘to’ geographic 2D coordinate reference systems.
 *
 * <table class="gigs">
 * <caption>Test description</caption>
 * <tr>
 *   <th>Test method:</th>
 *   <td><ul>
 *      <li>Invoke GIGS transformation “GIGS geogCRS B to GIGS geogCRS A (1)”, GIGS code 61196 in both
 *      directions and inspect results.</li>
 *      <li>For the first test point perform iterations of forward and reverse computations using the output
 *      of computation n as input into computation n+1, until the output coordinate values exceed more
 *      than 0.00000006° from the original calculated values (but no more than 1000 iterations).</li></ul>
 *   </td>
 * </tr><tr>
 *   <th>Test data folder:</th>
 *   <td><a href="https://github.com/IOGP-GIGS/GIGSTestDataset/blob/main/GIGSTestDatasetFiles/GIGS%205200%20Coordinate%20transformation%20test%20data/ASCII/GIGS_tfm_5213_3trnslt_Geog2D_input.txt">{@code GIGS_tfm_5213_3trnslt_Geog2D_input.txt}</a>
 * </tr><tr>
 *   <th>Tested API:</th>
 *   <td>{@link CoordinateOperationFactory#createOperation(CoordinateReferenceSystem, CoordinateReferenceSystem, OperationMethod)}.</td>
 * </tr><tr>
 *   <th>Expected result:</th>
 *   <td>One of two possible sets of results should agree to within 0.03m (30mm) or 0.0000003° of the
 *      Test Data. See file GIGS_tfm_5213_3trnslt_Geog2D_output_AbrMol or GIGS_tfm_5213_3trnslt_
 *      Geog2D_output_EPSGconcat. These two sets use alternative methods, both of which are valid
 *      for the transformation of geographic 2D CRSs. Report which of the coordinate transformation
 *      methods the application is using.
 *      <p>For the round trip calculation the initial calculated coordinates of the point should change by less
 *      than 0.006m (6mm) or 0.00000006° (before 1000 iterations).
 *      <p>Test result will be pass or fail. If fail, details of failure should be reported.</td>
 * </tr></table>
 *
 *
 * <h2>Usage example</h2>
 * In order to specify their factories and run the tests in a JUnit framework, implementers can
 * define a subclass in their own test suite as in the example below:
 *
 * {@snippet lang = "java":
 * public class MyTest extends Test5213 {
 *     public MyTest() {
 *         super(new MyFactories());
 *     }
 * }
 *}
 *
 * @author  Estelle Idée (Geomatys)
 * @version 1.0
 * @since   1.0
 */
@DisplayName("Geocentric translations (geographic 3D domain) transformation")
public abstract class Test5213 extends Series5000 {
    /**
     * Data about the CRS of the geocentric CRS.
     *
     * @see #createGeogCRSA(TestMethod)
     */
    final Test3205 geogCRSATest;

    /**
     * Data about the CRS of the geocentric CRS.
     *
     * @see #createGeogCRSB(TestMethod)
     */
    final Test3205 geogCRSBTest;

    /**
     * The Geocentric CRS created by this factory.
     */
    protected GeographicCRS geogCRSA;

    /**
     * The geocentric CRS created by the factory,
     * or {@code null} if not yet created or if geocentric CRS creation failed.
     */
    protected GeographicCRS geogCRSB;

    /**
     * Factory to use for building {@link Transformation} instances, or {@code null} if none.
     * This is the factory used by the {@link #convertPoint(double[])} ()} method.
     */
    protected final CoordinateOperationFactory copFactory;
    /**
     * Factory to use for building {@link CoordinateOperation} instances, or {@code null} if none.
     * This is the factory used by the {@link #createCoOp()} method.
     */
    protected final MathTransformFactory mtFactory;

    /**
     * Creates a new test using the given factories.
     * The factories needed by this class are {@link CRSFactory}, {@link CSFactory},
     * {@link DatumFactory}, {@link CoordinateOperationFactory}, {@link MathTransformFactory}
     * and {@link CRSAuthorityFactory}.
     * If a requested factory is {@code null}, then the tests which depend on it will be skipped.
     *
     * @param factories  factories for creating the instances to test.
     *
     * @throws FactoryException if an error occurred while creating the CRSs.
     */
    public Test5213(final Factories factories) throws FactoryException {
        super( 0.03, 0.0000003, 0.006, 0.00000006);

        copFactory  = factories.copFactory;
        mtFactory  = factories.mtFactory;

        geogCRSATest = new Test3205(factories);
        geogCRSATest.skipTests = true;
        geogCRSATest.skipIdentificationCheck = true;

        geogCRSBTest = new Test3205(factories);
        geogCRSBTest.skipTests = true;
        geogCRSBTest.skipIdentificationCheck = true;

        createCRSs();
        createCoOp();
    }

    /**
     * Method used in class instantiation to initialise the geocentric CRS and the geographic CRS
     * used in this test class.
     * <p>Geographic CRS B : GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277
     * <p>Geographic CRS A : GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326
     *
     * @throws FactoryException if an error occurred while creating a CRS.
     */
    @Override
    protected void createCRSs() throws FactoryException {
        createGeogCRSB(Test3205::GIGS_64005);
        createGeogCRSA(Test3205::GIGS_64003);
    }

    @Override
    protected abstract void createCoOp() throws FactoryException;

    /**
     * Creates a user-defined geographic CRS by executing the specified method from the {@link Test3205} class.
     *
     * @param  factory          the test method to use for creating the geocentric CRS.
     * @throws FactoryException if an error occurred while creating the geocentric CRS.
     */
    void createGeogCRSA(final TestMethod<Test3205> factory) throws FactoryException {
        factory.initialize(geogCRSATest);
        geogCRSA = (GeographicCRS) geogCRSATest.getIdentifiedObject();
    }

    /**
     * Creates a user-defined geocentric CRS by executing the specified method from the {@link Test3205} class.
     *
     * @param  factory          the test method to use for creating the geocentric CRS.
     * @throws FactoryException if an error occurred while creating the geocentric CRS.
     */
    void createGeogCRSB(final TestMethod<Test3205> factory) throws FactoryException {
        factory.initialize(geogCRSBTest);
        geogCRSB = (GeographicCRS) geogCRSBTest.getIdentifiedObject();
    }

    /**
     * Verifies the result of the conversion produced by {@link #convertPoint(double[])} ()}.
     *
     * @param expectedPoint the expected coordinates from the GIGS output file.
     * @param resPoint      the coordinates computed by the factory.
     */
    void verifyConversion(double[] expectedPoint, double[] resPoint) {
        assertArrayEquals(expectedPoint, resPoint, isRTC ? rtGeoTolerance : geoTolerance);
    }
}
