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
import org.junit.jupiter.api.Test;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.cs.CSFactory;
import org.opengis.referencing.datum.DatumFactory;
import org.opengis.referencing.operation.Conversion;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Verifies the software’s capabilities to perform conversions for the geocentric translation method.
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
 *      than 0.006m (6mm) from the original calculated values (before 1000 iterations).</li></ul>
 *   </td>
 * </tr><tr>
 *   <th>Test data folder:</th>
 *   <td><a href="https://github.com/IOGP-GIGS/GIGSTestDataset/blob/main/GIGSTestDatasetFiles/GIGS%205200%20Coordinate%20transformation%20test%20data/ASCII/GIGS_tfm_5211_3trnslt_Geocen_input.txt">{@code GIGS_tfm_5211_3trnslt_Geocen_input.txt}</a>
 * </tr><tr>
 *   <th>Tested API:</th>
 *   <td>{@link CoordinateOperationFactory#createOperation(CoordinateReferenceSystem, CoordinateReferenceSystem)}.</td>
 * </tr><tr>
 *   <th>Expected result:</th>
 *   <td>Results should agree to within 0.03m (30mm) or 0.0000003° of the Test Data..
 *       <p>See file <a href="https://github.com/IOGP-GIGS/GIGSTestDataset/blob/main/GIGSTestDatasetFiles/GIGS%205200%20Coordinate%20transformation%20test%20data/ASCII/GIGS_tfm_5211_3trnslt_Geocen_output.txt">{@code GIGS_tfm_5211_3trnslt_Geocen_output.txt}</a>.
 *       <p>For the round trip calculation the initial calculated coordinates of the point should change by less
 *      than 0.006m (6mm) or 0.00000006° (before 1000 iterations).
 *       <p>Test result will be pass or fail. If fail, details of failure should be reported.</td>
 * </tr></table>
 *
 *
 * <h2>Usage example</h2>
 * In order to specify their factories and run the tests in a JUnit framework, implementers can
 * define a subclass in their own test suite as in the example below:
 *
 * {@snippet lang = "java":
 * public class MyTest extends Test52111 {
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
@DisplayName("geocentric translation transformation")
public class Test5211 extends Series5000 {
    /**
     * Data about the CRS of the geographic CRS.
     *
     * @see #createGeogCRS(TestMethod)
     */
    final Test3205 geogCRSTest;

    /**
     * Data about the CRS of the geocentric CRS.
     *
     * @see #createGeocCRS(TestMethod)
     */
    final Test3205 geocCRSTest;

    /**
     * The geographic CRS created by this factory.
     */
    protected GeographicCRS geogCRS;

    /**
     * The geocentric CRS created by the factory,
     * or {@code null} if not yet created or if geocentric CRS creation failed.
     */
    protected GeocentricCRS geocCRS;

    /**
     * Factory to use for building {@link Conversion} instances, or {@code null} if none.
     * This is the factory used by the {@link #convertPoint(double[])} ()} method.
     */
    protected final CoordinateOperationFactory copFactory;

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
    public Test5211(final Factories factories) throws FactoryException {
        super( 0.03, 0.0000003, 0.006, 0.00000006);
        copFactory            = factories.copFactory;

        geogCRSTest = new Test3205(factories);
        geogCRSTest.skipTests = true;
        geogCRSTest.skipIdentificationCheck = true;

        geocCRSTest = new Test3205(factories);
        geocCRSTest.skipTests = true;
        geocCRSTest.skipIdentificationCheck = true;
        createCRSs();
        createCoOp();
    }

    /**
     * Method used in class instantiation to initialise the geocentric CRS and the geographic CRS
     * used in this test class.
     * <p>Geocentric CRS B : GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent
     * <p>Geocentric CRS A : GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978
     *
     * @throws FactoryException if an error occurred while creating a CRS.
     */
    @Override
    protected void createCRSs() throws FactoryException {
        // TODO : find a way to convert EPSG:4277 to cartesian/geocentric
        createGeocCRS(Test3205::GIGS_64005);
        createGeogCRS(Test3205::GIGS_64001);
    }

    @Override
    protected void createCoOp() throws FactoryException {
        forwardCoOp = copFactory.createOperation(geocCRS, geogCRS);
        reverseCoOp = copFactory.createOperation(geogCRS, geocCRS);
    }

    /**
     * Creates a user-defined geographic CRS by executing the specified method from the {@link Test3205} class.
     *
     * @param  factory          the test method to use for creating the geographic CRS.
     * @throws FactoryException if an error occurred while creating the geographic CRS.
     */
    void createGeogCRS(final TestMethod<Test3205> factory) throws FactoryException {
        factory.initialize(geogCRSTest);
        geogCRS = (GeographicCRS) geogCRSTest.getIdentifiedObject();
    }

    /**
     * Creates a user-defined geocentric CRS by executing the specified method from the {@link Test3205} class.
     *
     * @param  factory          the test method to use for creating the projected CRS.
     * @throws FactoryException if an error occurred while creating the projected CRS.
     */
    void createGeocCRS(final TestMethod<Test3205> factory) throws FactoryException {
        factory.initialize(geocCRSTest);
        geocCRS = (GeocentricCRS) geocCRSTest.getIdentifiedObject();
    }

    /**
     * Verifies the result of the conversion produced by {@link #convertPoint(double[])} ()}.
     *
     * @param expectedPoint the expected coordinates from the GIGS output file.
     * @param resPoint the coordinates computed by the factory.
     */
    void verifyConversion(double[] expectedPoint, double[] resPoint) {
        assertEquals(3, resPoint.length);
        assertArrayEquals(expectedPoint, resPoint, isRTC ? rtCartTolerance : cartTolerance);
    }

    /**
     * Tests “GIGS-5211-01” for Geocentric translation based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5211-01</b></li>
     *   <li>Geocentric X (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-962850.592</b></li>
     *   <li>Geocentric Y (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>555799.852</b></li>
     *   <li>Geocentric Z (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>6260304.653</b></li>
     *   <li>Latitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-962479.592</b></li>
     *   <li>Longitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>555687.852</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>6260738.653</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: Round Trip calculation point.
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5211-01 : REVERSE")
    public void GIGS_5211_01() throws TransformException {
        isRTC = true;
        isForward = false;
        final double[] originPoint = new double[]{-962479.592, 555687.852, 6260738.653};
        final double[] destinationPoint = new double[]{-962850.592, 555799.852, 6260304.653};
        convertAndVerifyRoundTripPoint(originPoint, destinationPoint);
    }

    /**
     * Tests “GIGS-5211-02” for Geocentric translation based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5211-02</b></li>
     *   <li>Geocentric X (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-962668.006</b></li>
     *   <li>Geocentric Y (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>555694.435</b></li>
     *   <li>Geocentric Z (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>6259108.961</b></li>
     *   <li>Latitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-962297.006</b></li>
     *   <li>Longitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>555582.435</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>6259542.961</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5211-02 : REVERSE")
    public void GIGS_5211_02() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-962297.006, 555582.435, 6259542.961};
        final double[] destinationPoint = new double[]{-962668.006, 555694.435, 6259108.961};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5211-03” for Geocentric translation based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5211-03</b></li>
     *   <li>Geocentric X (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-1598619.169</b></li>
     *   <li>Geocentric Y (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>2768889.623</b></li>
     *   <li>Geocentric Z (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>5500844.468</b></li>
     *   <li>Latitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-1598248.169</b></li>
     *   <li>Longitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>2768777.623</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>5501278.468</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5211-03 : FORWARD")
    public void GIGS_5211_03() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-1598619.169, 2768889.623, 5500844.468};
        final double[] destinationPoint = new double[]{-1598248.169, 2768777.623, 5501278.468};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5211-04” for Geocentric translation based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5211-04</b></li>
     *   <li>Geocentric X (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-1598394.169</b></li>
     *   <li>Geocentric Y (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>2768499.912</b></li>
     *   <li>Geocentric Z (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>5500065.045</b></li>
     *   <li>Latitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-1598023.169</b></li>
     *   <li>Longitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>2768387.912</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>5500499.045</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5211-04 : FORWARD")
    public void GIGS_5211_04() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-1598394.169, 2768499.912, 5500065.045};
        final double[] destinationPoint = new double[]{-1598023.169, 2768387.912, 5500499.045};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5211-05” for Geocentric translation based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5211-05</b></li>
     *   <li>Geocentric X (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>2763839.405</b></li>
     *   <li>Geocentric Y (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>4787864.865</b></li>
     *   <li>Geocentric Z (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>3170034.52</b></li>
     *   <li>Latitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>2764210.405</b></li>
     *   <li>Longitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>4787752.865</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>3170468.52</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5211-05 : REVERSE")
    public void GIGS_5211_05() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{2764210.405, 4787752.865, 3170468.52};
        final double[] destinationPoint = new double[]{2763839.405, 4787864.865, 3170034.52};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5211-06” for Geocentric translation based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5211-06</b></li>
     *   <li>Geocentric X (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>2763757.32</b></li>
     *   <li>Geocentric Y (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>4787722.688</b></li>
     *   <li>Geocentric Z (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>3169939.735</b></li>
     *   <li>Latitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>2764128.32</b></li>
     *   <li>Longitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>4787610.688</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>3170373.735</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5211-06 : REVERSE")
    public void GIGS_5211_06() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{2764128.32, 4787610.688, 3170373.735};
        final double[] destinationPoint = new double[]{2763757.32, 4787722.688, 3169939.735};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5211-07” for Geocentric translation based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5211-07</b></li>
     *   <li>Geocentric X (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>6377563.396</b></li>
     *   <li>Geocentric Y (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Geocentric Z (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Latitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>6377934.396</b></li>
     *   <li>Longitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-112</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>434</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5211-07 : FORWARD")
    public void GIGS_5211_07() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{6377563.396, 0, 0};
        final double[] destinationPoint = new double[]{6377934.396, -112, 434};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5211-08” for Geocentric translation based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5211-08</b></li>
     *   <li>Geocentric X (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>6374563.396</b></li>
     *   <li>Geocentric Y (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Geocentric Z (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Latitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>6374934.396</b></li>
     *   <li>Longitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-112</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>434</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5211-08 : FORWARD")
    public void GIGS_5211_08() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{6374563.396, 0, 0};
        final double[] destinationPoint = new double[]{6374934.396, -112, 434};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5211-09” for Geocentric translation based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5211-09</b></li>
     *   <li>Geocentric X (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>6367563.396</b></li>
     *   <li>Geocentric Y (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Geocentric Z (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Latitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>6367934.396</b></li>
     *   <li>Longitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-112</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>434</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5211-09 : FORWARD")
    public void GIGS_5211_09() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{6367563.396, 0, 0};
        final double[] destinationPoint = new double[]{6367934.396, -112, 434};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5211-10” for Geocentric translation based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5211-10</b></li>
     *   <li>Geocentric X (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>2763757.32</b></li>
     *   <li>Geocentric Y (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-4787498.688</b></li>
     *   <li>Geocentric Z (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-3170807.735</b></li>
     *   <li>Latitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>2764128.32</b></li>
     *   <li>Longitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-4787610.688</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-3170373.735</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5211-10 : REVERSE")
    public void GIGS_5211_10() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{2764128.32, -4787610.688, -3170373.735};
        final double[] destinationPoint = new double[]{2763757.32, -4787498.688, -3170807.735};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5211-11” for Geocentric translation based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5211-11</b></li>
     *   <li>Geocentric X (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>2763529.349</b></li>
     *   <li>Geocentric Y (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-4787103.831</b></li>
     *   <li>Geocentric Z (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-3170544.497</b></li>
     *   <li>Latitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>2763900.349</b></li>
     *   <li>Longitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-4787215.831</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-3170110.497</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5211-11 : REVERSE")
    public void GIGS_5211_11() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{2763900.349, -4787215.831, -3170110.497};
        final double[] destinationPoint = new double[]{2763529.349, -4787103.831, -3170544.497};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5211-12” for Geocentric translation based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5211-12</b></li>
     *   <li>Geocentric X (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>2763509.863</b></li>
     *   <li>Geocentric Y (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-4787070.081</b></li>
     *   <li>Geocentric Z (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-3170521.997</b></li>
     *   <li>Latitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>2763880.863</b></li>
     *   <li>Longitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-4787182.081</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-3170087.997</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5211-12 : REVERSE")
    public void GIGS_5211_12() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{2763880.863, -4787182.081, -3170087.997};
        final double[] destinationPoint = new double[]{2763509.863, -4787070.081, -3170521.997};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5211-13” for Geocentric translation based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5211-13</b></li>
     *   <li>Geocentric X (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-1598394.169</b></li>
     *   <li>Geocentric Y (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-2768499.912</b></li>
     *   <li>Geocentric Z (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-5500065.045</b></li>
     *   <li>Latitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-1598023.169</b></li>
     *   <li>Longitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-2768611.912</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-5499631.045</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5211-13 : FORWARD")
    public void GIGS_5211_13() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-1598394.169, -2768499.912, -5500065.045};
        final double[] destinationPoint = new double[]{-1598023.169, -2768611.912, -5499631.045};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5211-14” for Geocentric translation based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5211-14</b></li>
     *   <li>Geocentric X (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-1598169.169</b></li>
     *   <li>Geocentric Y (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-2768110.201</b></li>
     *   <li>Geocentric Z (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-5499285.622</b></li>
     *   <li>Latitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-1597798.169</b></li>
     *   <li>Longitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-2768222.201</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-5498851.622</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5211-14 : FORWARD")
    public void GIGS_5211_14() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-1598169.169, -2768110.201, -5499285.622};
        final double[] destinationPoint = new double[]{-1597798.169, -2768222.201, -5498851.622};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5211-15” for Geocentric translation based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5211-15</b></li>
     *   <li>Geocentric X (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-962668.006</b></li>
     *   <li>Geocentric Y (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-555470.435</b></li>
     *   <li>Geocentric Z (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-6259976.961</b></li>
     *   <li>Latitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-962297.006</b></li>
     *   <li>Longitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-555582.435</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-6259542.961</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5211-15 : REVERSE")
    public void GIGS_5211_15() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-962297.006, -555582.435, -6259542.961};
        final double[] destinationPoint = new double[]{-962668.006, -555470.435, -6259976.961};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5211-16” for Geocentric translation based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5211-16</b></li>
     *   <li>Geocentric X (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-962521.945</b></li>
     *   <li>Geocentric Y (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-555386.107</b></li>
     *   <li>Geocentric Z (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-6259020.462</b></li>
     *   <li>Latitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-962150.945</b></li>
     *   <li>Longitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-555498.107</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-6258586.462</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5211-16 : REVERSE")
    public void GIGS_5211_16() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-962150.945, -555498.107, -6258586.462};
        final double[] destinationPoint = new double[]{-962521.945, -555386.107, -6259020.462};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5211-17” for Geocentric translation based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5211-17</b></li>
     *   <li>Geocentric X (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-962169.295</b></li>
     *   <li>Geocentric Y (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-555182.505</b></li>
     *   <li>Geocentric Z (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-6256711.087</b></li>
     *   <li>Latitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-961798.295</b></li>
     *   <li>Longitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-555294.505</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-6256277.087</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5211-17 : REVERSE")
    public void GIGS_5211_17() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-961798.295, -555294.505, -6256277.087};
        final double[] destinationPoint = new double[]{-962169.295, -555182.505, -6256711.087};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5211-18” for Geocentric translation based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5211-18</b></li>
     *   <li>Geocentric X (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-2187707.719</b></li>
     *   <li>Geocentric Y (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Geocentric Z (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>5970583.093</b></li>
     *   <li>Latitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-2187336.719</b></li>
     *   <li>Longitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-112</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>5971017.093</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5211-18 : FORWARD")
    public void GIGS_5211_18() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-2187707.719, 0, 5970583.093};
        final double[] destinationPoint = new double[]{-2187336.719, -112, 5971017.093};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5211-19” for Geocentric translation based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5211-19</b></li>
     *   <li>Geocentric X (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-2905069.555</b></li>
     *   <li>Geocentric Y (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-2904586.555</b></li>
     *   <li>Geocentric Z (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>4862355.038</b></li>
     *   <li>Latitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-2904698.555</b></li>
     *   <li>Longitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-2904698.555</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>4862789.038</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5211-19 : REVERSE")
    public void GIGS_5211_19() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-2904698.555, -2904698.555, 4862789.038};
        final double[] destinationPoint = new double[]{-2905069.555, -2904586.555, 4862355.038};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5211-20” for Geocentric translation based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5211-20</b></li>
     *   <li>Geocentric X (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Geocentric Y (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-5783481.614</b></li>
     *   <li>Geocentric Z (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>2678892.11</b></li>
     *   <li>Latitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>371</b></li>
     *   <li>Longitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-5783593.614</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>2679326.11</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5211-20 : FORWARD")
    public void GIGS_5211_20() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{0, -5783481.614, 2678892.11};
        final double[] destinationPoint = new double[]{371, -5783593.614, 2679326.11};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5211-21” for Geocentric translation based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5211-21</b></li>
     *   <li>Geocentric X (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>6377766</b></li>
     *   <li>Geocentric Y (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>112</b></li>
     *   <li>Geocentric Z (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-434</b></li>
     *   <li>Latitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>6378137</b></li>
     *   <li>Longitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>0</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>0</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5211-21 : REVERSE")
    public void GIGS_5211_21() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{6378137, 0, 0};
        final double[] destinationPoint = new double[]{6377766, 112, -434};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5211-22” for Geocentric translation based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5211-22</b></li>
     *   <li>Geocentric X (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-4087466.478</b></li>
     *   <li>Geocentric Y (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>2977579.559</b></li>
     *   <li>Geocentric Z (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-3875891.429</b></li>
     *   <li>Latitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-4087095.478</b></li>
     *   <li>Longitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>2977467.559</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-3875457.429</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5211-22 : FORWARD")
    public void GIGS_5211_22() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-4087466.478, 2977579.559, -3875891.429};
        final double[] destinationPoint = new double[]{-4087095.478, 2977467.559, -3875457.429};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5211-23” for Geocentric translation based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5211-23</b></li>
     *   <li>Geocentric X (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-4086290.959</b></li>
     *   <li>Geocentric Y (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>2976723.233</b></li>
     *   <li>Geocentric Z (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-3874769.274</b></li>
     *   <li>Latitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-4085919.959</b></li>
     *   <li>Longitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>2976611.233</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-3874335.274</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5211-23 : FORWARD")
    public void GIGS_5211_23() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-4086290.959, 2976723.233, -3874769.274};
        final double[] destinationPoint = new double[]{-4085919.959, 2976611.233, -3874335.274};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5211-24” for Geocentric translation based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5211-24</b></li>
     *   <li>Geocentric X (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-4084371.165</b></li>
     *   <li>Geocentric Y (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>2975324.729</b></li>
     *   <li>Geocentric Z (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-3872936.631</b></li>
     *   <li>Latitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-4084000.165</b></li>
     *   <li>Longitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>2975212.729</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-3872502.631</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5211-24 : FORWARD")
    public void GIGS_5211_24() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-4084371.165, 2975324.729, -3872936.631};
        final double[] destinationPoint = new double[]{-4084000.165, 2975212.729, -3872502.631};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5211-25” for Geocentric translation based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5211-25</b></li>
     *   <li>Geocentric X (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-4079891.647</b></li>
     *   <li>Geocentric Y (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>2972061.553</b></li>
     *   <li>Geocentric Z (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-3868660.465</b></li>
     *   <li>Latitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-4079520.647</b></li>
     *   <li>Longitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>2971949.553</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-3868226.465</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5211-25 : FORWARD")
    public void GIGS_5211_25() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-4079891.647, 2972061.553, -3868660.465};
        final double[] destinationPoint = new double[]{-4079520.647, 2971949.553, -3868226.465};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5211-26” for Geocentric translation based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5211-26</b></li>
     *   <li>Geocentric X (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-2905069.555</b></li>
     *   <li>Geocentric Y (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>2904810.555</b></li>
     *   <li>Geocentric Z (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-4863223.038</b></li>
     *   <li>Latitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-2904698.555</b></li>
     *   <li>Longitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>2904698.555</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-4862789.038</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5211-26 : REVERSE")
    public void GIGS_5211_26() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-2904698.555, 2904698.555, -4862789.038};
        final double[] destinationPoint = new double[]{-2905069.555, 2904810.555, -4863223.038};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5211-27” for Geocentric translation based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5211-27</b></li>
     *   <li>Geocentric X (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-2187707.719</b></li>
     *   <li>Geocentric Y (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Geocentric Z (GIGS geocenCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-5970583.093</b></li>
     *   <li>Latitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-2187336.719</b></li>
     *   <li>Longitude (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-112</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978): <b>-5970149.093</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5211-27 : FORWARD")
    public void GIGS_5211_27() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-2187707.719, 0, -5970583.093};
        final double[] destinationPoint = new double[]{-2187336.719, -112, -5970149.093};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }
}
