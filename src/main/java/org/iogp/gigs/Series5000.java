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
import org.opengis.referencing.ObjectFactory;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.opentest4j.AssertionFailedError;

import static org.junit.jupiter.api.Assertions.assertNull;


/**
 * Base class for tests of conversions and coordinate transformations (5100 and 5200 series).
 * The test procedures in this series evaluate the software’s capabilities
 * to perform conversions (map projections) and coordinate transformations.
 * The conversions and coordinate transformations are performed using {@link org.opengis.referencing.operation.CoordinateOperationFactory}.
 *
 * @author  Estelle Idée
 * @version 1.0
 * @since   1.0
 */
public abstract class Series5000 extends IntegrityTest {

    /**
     * Whether the objects created by the tested {@link ObjectFactory} use the specified values <i>as-is</i>.
     * This flag should be set to {@code false} if the factory performs any of the following operations:
     *
     * <ul>
     *   <li>Convert numerical values from user-provided linear units to metres.</li>
     *   <li>Convert numerical values from user-provided angular units to degrees.</li>
     *   <li>Change ellipsoid second defining parameter
     *       (e.g. from <i>semi-major axis length</i> to an equivalent <i>inverse flattening factor</i>).</li>
     *   <li>Change map projection parameters
     *       (e.g. from <i>standard parallel</i> to an equivalent <i>scale factor</i>).</li>
     *   <li>Any other change that preserve numeric equivalence.</li>
     * </ul>
     *
     * If the factory does not perform any of the above conversions, then this flag can be {@code true}.
     */
    protected boolean isFactoryPreservingUserValues;

    /**
     * If {@code true}, initialize the data but do not run the test.
     */
    boolean skipTests;

    /**
     * Cartesian tolerance
     */
    double cartTolerance;

    /**
     * Geographic tolerance
     */
    double geoTolerance;

    /**
     * Round Trip Cartesian tolerance
     */
    double rtCartTolerance;

    /**
     * Round Trip Geographic Tolerance
     */
    double rtGeoTolerance;

    /**
     * Whether the test shall be a round trip conversion/transformation.
     */
    boolean isRTC;

    /**
     * Whether the conversion or transformation is forward or reverse direction.
     */
    boolean isForward;

    /**
     * The forward operation created by the factory
     * or {@code null} if not yet created or if operation creation failed.
     */
    CoordinateOperation forwardCoOp;

    /**
     * The reverse operation created by the factory
     * or {@code null} if not yet created or if operation creation failed.
     */
    CoordinateOperation reverseCoOp;

    /**
     * Creates a new test.
     *
     * @param cartTolerance the Cartesian tolerance to use for the tests.
     * @param geoTolerance the Geographic tolerance to use for the tests.
     * @param rtCartTolerance the Round Trip Cartesian tolerance to use for the tests.
     * @param rtGeoTolerance the Round Trip Geographic tolerance to use for the tests.
     */
    Series5000(final double cartTolerance, final double geoTolerance, final double rtCartTolerance, final double rtGeoTolerance) {
        initialize();
        this.cartTolerance = cartTolerance;
        this.geoTolerance = geoTolerance;
        this.rtCartTolerance = rtCartTolerance;
        this.rtGeoTolerance = rtGeoTolerance;
    }

    /**
     * Create the test @{@link org.opengis.referencing.crs.CoordinateReferenceSystem}.
     *
     * @throws FactoryException if an error occurred while creating the CRSs.
     */
    protected abstract void createCRSs() throws FactoryException;

    /**
     * Create the {@link CoordinateOperation} for the forward and reverse tests.
     *
     * @throws FactoryException if an error occurred while creating the {@link CoordinateOperation}.
     */
    protected abstract void createCoOp() throws FactoryException;

    /**
     * Returns the configuration keys for enabling or disabling optional aspects to be verified.
     * This method does not clone the returned array. It is okay because this method is not public.
     */
    @Override
    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    final Configuration.Key<Boolean>[] getOptionKeys() {
        return OPTION_KEYS;
    }

    /**
     * The array returned by {@link #getOptionKeys()}.
     * Shall not be modified, because it will not be cloned.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static final Configuration.Key<Boolean>[] OPTION_KEYS = new Configuration.Key[] {
            /* [0] */ Configuration.Key.isFactoryPreservingUserValues};

    /**
     * Enables or disables an optional aspect to be verified.
     * The {@code key} argument value shall be an index in the {@link #OPTION_KEYS} array.
     */
    @Override
    final void setOptionEnabled(final int key, final boolean value) {
        switch (key) {
            case  0: isFactoryPreservingUserValues = value; break;
            default: throw new AssertionError(key);
        }
    }

    /**
     * Returns information about the configuration of the test which has been run.
     * This method returns a map containing:
     *
     * <ul>
     *   <li>All the following values associated to the {@link Configuration.Key} of the same name:
     *     <ul>
     *       <li>{@link #isFactoryPreservingUserValues}</li>
     *       <li>The factories used by the test (provided by subclasses)</li>
     *     </ul>
     *   </li>
     * </ul>
     *
     * @return the configuration of the test being run.
     */
    @Override
    Configuration configuration() {
        final Configuration op = super.configuration();
        assertNull(op.put(Configuration.Key.isFactoryPreservingUserValues, isFactoryPreservingUserValues));
        return op;
    }

    /**
     * Copies the configuration from the given test class. This method is invoked when a test depends on other tests,
     * in which case the tests need to be run with the same configuration in order to get data.
     *
     * @param  source  the test class from which to copy the configuration.
     */
    final void copyConfigurationFrom(final Series5000 source) {
        isFactoryPreservingUserValues = source.isFactoryPreservingUserValues;
        skipIdentificationCheck |= source.skipIdentificationCheck;
        skipTests = false;
    }

    /**
     * Convert the point to the output CRS.
     *
     * @param originPoint the point to convert to the destination CRS.
     * @return the result point from the conversion/transformation
     *
     * @throws TransformException if an error occurred while converting the given point.
     */
    double[] convertPoint(final double[] originPoint) throws TransformException {
        final CoordinateOperation distop = isForward ? forwardCoOp : reverseCoOp;

        final MathTransform distTrs = distop.getMathTransform();
        final double[] distRes = new double[distTrs.getTargetDimensions()];
        distTrs.transform(originPoint, 0, distRes, 0, 1);
        return distRes;
    }

    /**
     * Convert the point to the output CRS.
     *
     * @param operation the operation to use to convert the point.
     * @param originPoint the point to convert to the destination CRS.
     * @return the result point from the conversion/transformation
     *
     * @throws TransformException if an error occurred while converting the given point.
     */
    double[] convertPoint(final CoordinateOperation operation, final double[] originPoint) throws TransformException {
        final MathTransform distTrs = operation.getMathTransform();
        final double[] distRes = new double[distTrs.getTargetDimensions()];
        distTrs.transform(originPoint, 0, distRes, 0, 1);
        return distRes;
    }

    /**
     * Verifies the result of the conversion produced by {@link #convertPoint(double[])} ()}.
     *
     * @param expectedPoint the expected coordinates from the GIGS output file.
     * @param resPoint the coordinates computed by the factory.
     */
    abstract void verifyConversion(double[] expectedPoint, double[] resPoint);


    /**
     * Performs a round trip conversion
     *
     * @param originPoint the point to convert to the destination CRS.
     * @param destinationPoint the expected coordinates from the GIGS output file.
     * @throws TransformException if an error occurred while converting the given point.
     */
    void convertAndVerifyRoundTripPoint(final double[] originPoint, final double[] destinationPoint) throws TransformException {
        double[] res = originPoint;
        int n = 0;
        while (n < 1000) {
            res = convertPoint(res);
            try {
                verifyConversion(destinationPoint, res);
            } catch (AssertionFailedError e) {
                throw new AssertionFailedError(" Fails after " + n + " iterations. " + e.getMessage());
            }
            n++;
            isForward = !isForward;
            res = convertPoint(res);
            n++;
            isForward = !isForward;
        }
        System.out.println("n = " + n);
    }

    /**
     * Performs a round trip conversion
     *
     * @param operation1 the operation to use to convert/transform the point coordinates from origin CRS.
     * @param operation2 the operation to use to convert/transform the point coordinates back to origin CRS.
     * @param originPoint the point to convert to the destination CRS.
     * @param destinationPoint the expected coordinates from the GIGS output file.
     * @throws TransformException if an error occurred while converting the given point.
     */
    void convertAndVerifyRoundTripPoint(final CoordinateOperation operation1, final CoordinateOperation operation2, final double[] originPoint, final double[] destinationPoint) throws TransformException {
        double[] res = originPoint;
        int n = 0;
        while (n < 1000) {
            res = convertPoint(operation1, res);
            try {
                verifyConversion(destinationPoint, res);
            } catch (AssertionFailedError e) {
                throw new AssertionFailedError(" Fails after " + n + " iterations. " + e.getMessage());
            }
            n++;
            res = convertPoint(operation2, res);
            n++;
        }
        System.out.println("n = " + n);
    }

}
