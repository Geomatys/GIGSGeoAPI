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
import org.opengis.referencing.operation.*;
import org.opengis.util.FactoryException;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Verifies the software’s capabilities to perform conversions for the geographic/geocentric method.
 *
 * <table class="gigs">
 * <caption>Test description</caption>
 * <tr>
 *   <th>Test method:</th>
 *   <td><ul>
 *      <li>Invoke coordinate conversions in both directions and inspect results</li>
 *      <li>For the first and last test point, perform iterations of forward and reverse computations using the
 *      output of computation n as input into computation n+1, until the output coordinate values exceed
 *      more than 0.006m (6mm) or 0.00000006° from the original calculated values (but no more than
 *      1000 iterations).</li></ul>
 *   </td>
 * </tr><tr>
 *   <th>Test data folder:</th>
 *   <td><a href="https://github.com/IOGP-GIGS/GIGSTestDataset/blob/main/GIGSTestDatasetFiles/GIGS%205200%20Coordinate%20transformation%20test%20data/ASCII/GIGS_tfm_5201_GeogGeocen_input.txt">{@code GIGS_tfm_5201_GeogGeocen_input.txt}</a>
 * </tr><tr>
 *   <th>Tested API:</th>
 *   <td>{@link CoordinateOperationFactory#createOperation(CoordinateReferenceSystem, CoordinateReferenceSystem)}.</td>
 * </tr><tr>
 *   <th>Expected result:</th>
 *   <td>Results should agree to within 0.01m (10mm) or 0.00000009° of the Test Data.
 *       See file <a href="https://github.com/IOGP-GIGS/GIGSTestDataset/blob/main/GIGSTestDatasetFiles/GIGS%205200%20Coordinate%20transformation%20test%20data/ASCII/GIGS_tfm_5201_GeogGeocen_output.txt">{@code GIGS_tfm_5201_GeogGeocen_output.txt}</a>.
 *       For the round trip calculation the initial calculated coordinates of the point should change by less
 *       than 0.006m (6mm) or 0.00000006° (before 1000 iterations).
 *       Test result will be pass or fail. If fail, details of failure should be reported.</td>
 * </tr></table>
 *
 *
 * <h2>Usage example</h2>
 * In order to specify their factories and run the tests in a JUnit framework, implementers can
 * define a subclass in their own test suite as in the example below:
 *
 * {@snippet lang = "java":
 * public class MyTest extends Test52011 {
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
@DisplayName("Geographic/geocentric transformation")
public class Test5201 extends Series5000 {
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
     * and {@link org.opengis.referencing.crs.CRSAuthorityFactory}.
     * If a requested factory is {@code null}, then the tests which depend on it will be skipped.
     *
     * @param factories  factories for creating the instances to test.
     *
     * @throws FactoryException if an error occurred while creating the CRSs.
     */
    public Test5201(final Factories factories) throws FactoryException {
        super( 0.01, 0.0003, 0.006, 0.00000006);
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
     * <p>Geocentric CRS : GIGS CRS Code 64001; GIGS geocenCRS A; WGS 84; metre; EPSG CRS code 4978
     * <p>Geographic CRS : GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979
     *
     * @throws FactoryException if an error occurred while creating a CRS.
     */
    @Override
    protected void createCRSs() throws FactoryException {
        createGeocCRS(Test3205::GIGS_64001);
        createGeogCRS(Test3205::GIGS_64002);
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
        if (isForward) {
            assertArrayEquals(Arrays.copyOf(expectedPoint, 2), Arrays.copyOf(resPoint, 2), isRTC ? rtGeoTolerance : geoTolerance);
            assertEquals(expectedPoint[2], resPoint[2], isRTC ? rtCartTolerance : cartTolerance);
        } else {
            assertArrayEquals(expectedPoint, resPoint, isRTC ? rtCartTolerance : cartTolerance);
        }
    }

    /**
     * Tests “GIGS-5201-01” for Geographic Geocentric transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5201-01</b></li>
     *   <li>Geocentric X (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>-962479.592</b></li>
     *   <li>Geocentric Y (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>555687.852</b></li>
     *   <li>Geocentric Z (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); metre; EPSG CRS code 4813): <b>6260738.653</b></li>
     *   <li>Latitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>80</b></li>
     *   <li>Longitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>150</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>1214.137</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: Round Trip calculation point.
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5201-01 : REVERSE")
    public void GIGS_5201_01() throws TransformException {
        isRTC = true;
        isForward = false;
        final double[] originPoint = new double[]{80, 150, 1214.137};
        final double[] destinationPoint = new double[]{-962479.592, 555687.852, 6260738.653};
        convertAndVerifyRoundTripPoint(originPoint, destinationPoint);
    }

    /**
     * Tests “GIGS-5201-02” for Geographic Geocentric transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5201-02</b></li>
     *   <li>Geocentric X (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>-962297.006</b></li>
     *   <li>Geocentric Y (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>555582.435</b></li>
     *   <li>Geocentric Z (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); metre; EPSG CRS code 4813): <b>6259542.961</b></li>
     *   <li>Latitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>80</b></li>
     *   <li>Longitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>150</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>0</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5201-02 : REVERSE")
    public void GIGS_5201_02() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{80, 150, 0};
        final double[] destinationPoint = new double[]{-962297.006, 555582.435, 6259542.961};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5201-03” for Geographic Geocentric transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5201-03</b></li>
     *   <li>Geocentric X (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>-1598248.169</b></li>
     *   <li>Geocentric Y (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>2768777.623</b></li>
     *   <li>Geocentric Z (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); metre; EPSG CRS code 4813): <b>5501278.468</b></li>
     *   <li>Latitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>60.00475191</b></li>
     *   <li>Longitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>119.9952454</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>619.632</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5201-03 : FORWARD")
    public void GIGS_5201_03() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-1598248.169, 2768777.623, 5501278.468};
        final double[] destinationPoint = new double[]{60.00475191, 119.9952454, 619.632};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5201-04” for Geographic Geocentric transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5201-04</b></li>
     *   <li>Geocentric X (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>-1598023.169</b></li>
     *   <li>Geocentric Y (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>2768387.912</b></li>
     *   <li>Geocentric Z (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); metre; EPSG CRS code 4813): <b>5500499.045</b></li>
     *   <li>Latitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>60.00475258</b></li>
     *   <li>Longitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>119.9952447</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-280.368</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5201-04 : FORWARD")
    public void GIGS_5201_04() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-1598023.169, 2768387.912, 5500499.045};
        final double[] destinationPoint = new double[]{60.00475258, 119.9952447, -280.368};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5201-05” for Geographic Geocentric transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5201-05</b></li>
     *   <li>Geocentric X (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>2764210.405</b></li>
     *   <li>Geocentric Y (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>4787752.865</b></li>
     *   <li>Geocentric Z (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); metre; EPSG CRS code 4813): <b>3170468.52</b></li>
     *   <li>Latitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>30</b></li>
     *   <li>Longitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>60</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>189.569</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5201-05 : REVERSE")
    public void GIGS_5201_05() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{30, 60, 189.569};
        final double[] destinationPoint = new double[]{2764210.405, 4787752.865, 3170468.52};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5201-06” for Geographic Geocentric transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5201-06</b></li>
     *   <li>Geocentric X (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>2764128.32</b></li>
     *   <li>Geocentric Y (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>4787610.688</b></li>
     *   <li>Geocentric Z (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); metre; EPSG CRS code 4813): <b>3170373.735</b></li>
     *   <li>Latitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>30</b></li>
     *   <li>Longitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>60</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>0</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5201-06 : REVERSE")
    public void GIGS_5201_06() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{30, 60, 0};
        final double[] destinationPoint = new double[]{2764128.32, 4787610.688, 3170373.735};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5201-07” for Geographic Geocentric transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5201-07</b></li>
     *   <li>Geocentric X (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>6377934.396</b></li>
     *   <li>Geocentric Y (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>-112</b></li>
     *   <li>Geocentric Z (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); metre; EPSG CRS code 4813): <b>434</b></li>
     *   <li>Latitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>0.00392509</b></li>
     *   <li>Longitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-0.00100615</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-202.588</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5201-07 : FORWARD")
    public void GIGS_5201_07() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{6377934.396, -112, 434};
        final double[] destinationPoint = new double[]{0.00392509, -0.00100615, -202.588};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5201-08” for Geographic Geocentric transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5201-08</b></li>
     *   <li>Geocentric X (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>6374934.396</b></li>
     *   <li>Geocentric Y (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>-112</b></li>
     *   <li>Geocentric Z (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); metre; EPSG CRS code 4813): <b>434</b></li>
     *   <li>Latitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>0.00392695</b></li>
     *   <li>Longitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-0.00100662</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-3202.588</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5201-08 : FORWARD")
    public void GIGS_5201_08() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{6374934.396, -112, 434};
        final double[] destinationPoint = new double[]{0.00392695, -0.00100662, -3202.588};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5201-09” for Geographic Geocentric transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5201-09</b></li>
     *   <li>Geocentric X (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>6367934.396</b></li>
     *   <li>Geocentric Y (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>-112</b></li>
     *   <li>Geocentric Z (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); metre; EPSG CRS code 4813): <b>434</b></li>
     *   <li>Latitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>0.00393129</b></li>
     *   <li>Longitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-0.00100773</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-10202.588</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5201-09 : FORWARD")
    public void GIGS_5201_09() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{6367934.396, -112, 434};
        final double[] destinationPoint = new double[]{0.00393129, -0.00100773, -10202.588};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5201-10” for Geographic Geocentric transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5201-10</b></li>
     *   <li>Geocentric X (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>2764128.32</b></li>
     *   <li>Geocentric Y (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>-4787610.688</b></li>
     *   <li>Geocentric Z (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); metre; EPSG CRS code 4813): <b>-3170373.735</b></li>
     *   <li>Latitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-30</b></li>
     *   <li>Longitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-60</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>0</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5201-10 : REVERSE")
    public void GIGS_5201_10() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-30, -60, 0};
        final double[] destinationPoint = new double[]{2764128.32, -4787610.688, -3170373.735};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5201-11” for Geographic Geocentric transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5201-11</b></li>
     *   <li>Geocentric X (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>2763900.349</b></li>
     *   <li>Geocentric Y (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>-4787215.831</b></li>
     *   <li>Geocentric Z (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); metre; EPSG CRS code 4813): <b>-3170110.497</b></li>
     *   <li>Latitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-30</b></li>
     *   <li>Longitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-60</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-526.476</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5201-11 : REVERSE")
    public void GIGS_5201_11() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-30, -60, -526.476};
        final double[] destinationPoint = new double[]{2763900.349, -4787215.831, -3170110.497};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5201-12” for Geographic Geocentric transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5201-12</b></li>
     *   <li>Geocentric X (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>2763880.863</b></li>
     *   <li>Geocentric Y (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>-4787182.081</b></li>
     *   <li>Geocentric Z (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); metre; EPSG CRS code 4813): <b>-3170087.997</b></li>
     *   <li>Latitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-30</b></li>
     *   <li>Longitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-60</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-571.476</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5201-12 : REVERSE")
    public void GIGS_5201_12() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-30, -60, -571.476};
        final double[] destinationPoint = new double[]{2763880.863, -4787182.081, -3170087.997};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5201-13” for Geographic Geocentric transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5201-13</b></li>
     *   <li>Geocentric X (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>-1598023.169</b></li>
     *   <li>Geocentric Y (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>-2768611.912</b></li>
     *   <li>Geocentric Z (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); metre; EPSG CRS code 4813): <b>-5499631.045</b></li>
     *   <li>Latitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-59.99934884</b></li>
     *   <li>Longitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-119.9932376</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-935.1</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5201-13 : FORWARD")
    public void GIGS_5201_13() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-1598023.169, -2768611.912, -5499631.045};
        final double[] destinationPoint = new double[]{-59.99934884, -119.9932376, -935.1};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5201-14” for Geographic Geocentric transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5201-14</b></li>
     *   <li>Geocentric X (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>-1597798.169</b></li>
     *   <li>Geocentric Y (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>-2768222.201</b></li>
     *   <li>Geocentric Z (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); metre; EPSG CRS code 4813): <b>-5498851.622</b></li>
     *   <li>Latitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-59.99934874</b></li>
     *   <li>Longitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-119.9932366</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-1835.1</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5201-14 : FORWARD")
    public void GIGS_5201_14() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-1597798.169, -2768222.201, -5498851.622};
        final double[] destinationPoint = new double[]{-59.99934874, -119.9932366, -1835.1};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5201-15” for Geographic Geocentric transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5201-15</b></li>
     *   <li>Geocentric X (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>-962297.006</b></li>
     *   <li>Geocentric Y (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>-555582.435</b></li>
     *   <li>Geocentric Z (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); metre; EPSG CRS code 4813): <b>-6259542.961</b></li>
     *   <li>Latitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-80</b></li>
     *   <li>Longitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-150</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>0</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5201-15 : REVERSE")
    public void GIGS_5201_15() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-80, -150, 0};
        final double[] destinationPoint = new double[]{-962297.006, -555582.435, -6259542.961};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5201-16” for Geographic Geocentric transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5201-16</b></li>
     *   <li>Geocentric X (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>-962150.945</b></li>
     *   <li>Geocentric Y (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>-555498.107</b></li>
     *   <li>Geocentric Z (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); metre; EPSG CRS code 4813): <b>-6258586.462</b></li>
     *   <li>Latitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-80</b></li>
     *   <li>Longitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-150</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-971.255</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5201-16 : REVERSE")
    public void GIGS_5201_16() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-80, -150, -971.255};
        final double[] destinationPoint = new double[]{-962150.945, -555498.107, -6258586.462};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5201-17” for Geographic Geocentric transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5201-17</b></li>
     *   <li>Geocentric X (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>-961798.295</b></li>
     *   <li>Geocentric Y (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>-555294.505</b></li>
     *   <li>Geocentric Z (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); metre; EPSG CRS code 4813): <b>-6256277.087</b></li>
     *   <li>Latitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-80</b></li>
     *   <li>Longitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-150</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-3316.255</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5201-17 : REVERSE")
    public void GIGS_5201_17() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-80, -150, -3316.255};
        final double[] destinationPoint = new double[]{-961798.295, -555294.505, -6256277.087};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5201-18” for Geographic Geocentric transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5201-18</b></li>
     *   <li>Geocentric X (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>-2187336.719</b></li>
     *   <li>Geocentric Y (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>-112</b></li>
     *   <li>Geocentric Z (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); metre; EPSG CRS code 4813): <b>5971017.093</b></li>
     *   <li>Latitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>70.00490733</b></li>
     *   <li>Longitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-179.9970662</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-223.618</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5201-18 : FORWARD")
    public void GIGS_5201_18() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-2187336.719, -112, 5971017.093};
        final double[] destinationPoint = new double[]{70.00490733, -179.9970662, -223.618};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5201-19” for Geographic Geocentric transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5201-19</b></li>
     *   <li>Geocentric X (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>-2904698.555</b></li>
     *   <li>Geocentric Y (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>-2904698.555</b></li>
     *   <li>Geocentric Z (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); metre; EPSG CRS code 4813): <b>4862789.038</b></li>
     *   <li>Latitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>50</b></li>
     *   <li>Longitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-135</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>0</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5201-19 : REVERSE")
    public void GIGS_5201_19() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{50, -135, 0};
        final double[] destinationPoint = new double[]{-2904698.555, -2904698.555, 4862789.038};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5201-20” for Geographic Geocentric transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5201-20</b></li>
     *   <li>Geocentric X (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>371</b></li>
     *   <li>Geocentric Y (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>-5783593.614</b></li>
     *   <li>Geocentric Z (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); metre; EPSG CRS code 4813): <b>2679326.11</b></li>
     *   <li>Latitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>25.00366329</b></li>
     *   <li>Longitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-89.99632465</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-274.729</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5201-20 : FORWARD")
    public void GIGS_5201_20() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{371, -5783593.614, 2679326.11};
        final double[] destinationPoint = new double[]{25.00366329, -89.99632465, -274.729};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5201-21” for Geographic Geocentric transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5201-21</b></li>
     *   <li>Geocentric X (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>6378137</b></li>
     *   <li>Geocentric Y (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>0</b></li>
     *   <li>Geocentric Z (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); metre; EPSG CRS code 4813): <b>0</b></li>
     *   <li>Latitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>0</b></li>
     *   <li>Longitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>0</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>0</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5201-21 : REVERSE")
    public void GIGS_5201_21() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{0, 0, 0};
        final double[] destinationPoint = new double[]{6378137, 0, 0};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5201-22” for Geographic Geocentric transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5201-22</b></li>
     *   <li>Geocentric X (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>-4087095.478</b></li>
     *   <li>Geocentric Y (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>2977467.559</b></li>
     *   <li>Geocentric Z (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); metre; EPSG CRS code 4813): <b>-3875457.429</b></li>
     *   <li>Latitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-37.65282217</b></li>
     *   <li>Longitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>143.9264925</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>737.718</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5201-22 : FORWARD")
    public void GIGS_5201_22() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-4087095.478, 2977467.559, -3875457.429};
        final double[] destinationPoint = new double[]{-37.65282217, 143.9264925, 737.718};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5201-23” for Geographic Geocentric transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5201-23</b></li>
     *   <li>Geocentric X (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>-4085919.959</b></li>
     *   <li>Geocentric Y (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>2976611.233</b></li>
     *   <li>Geocentric Z (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); metre; EPSG CRS code 4813): <b>-3874335.274</b></li>
     *   <li>Latitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-37.65282206</b></li>
     *   <li>Longitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>143.9264921</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-1099.229</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5201-23 : FORWARD")
    public void GIGS_5201_23() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-4085919.959, 2976611.233, -3874335.274};
        final double[] destinationPoint = new double[]{-37.65282206, 143.9264921, -1099.229};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5201-24” for Geographic Geocentric transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5201-24</b></li>
     *   <li>Geocentric X (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>-4084000.165</b></li>
     *   <li>Geocentric Y (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>2975212.729</b></li>
     *   <li>Geocentric Z (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); metre; EPSG CRS code 4813): <b>-3872502.631</b></li>
     *   <li>Latitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-37.65282187</b></li>
     *   <li>Longitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>143.9264914</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-4099.229</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5201-24 : FORWARD")
    public void GIGS_5201_24() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-4084000.165, 2975212.729, -3872502.631};
        final double[] destinationPoint = new double[]{-37.65282187, 143.9264914, -4099.229};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5201-25” for Geographic Geocentric transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5201-25</b></li>
     *   <li>Geocentric X (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>-4079520.647</b></li>
     *   <li>Geocentric Y (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>2971949.553</b></li>
     *   <li>Geocentric Z (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); metre; EPSG CRS code 4813): <b>-3868226.465</b></li>
     *   <li>Latitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-37.65282143</b></li>
     *   <li>Longitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>143.9264898</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-11099.229</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5201-25 : FORWARD")
    public void GIGS_5201_25() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-4079520.647, 2971949.553, -3868226.465};
        final double[] destinationPoint = new double[]{-37.65282143, 143.9264898, -11099.229};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5201-26” for Geographic Geocentric transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5201-26</b></li>
     *   <li>Geocentric X (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>-2904698.555</b></li>
     *   <li>Geocentric Y (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>2904698.555</b></li>
     *   <li>Geocentric Z (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); metre; EPSG CRS code 4813): <b>-4862789.038</b></li>
     *   <li>Latitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-50</b></li>
     *   <li>Longitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>135</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>0</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5201-26 : REVERSE")
    public void GIGS_5201_26() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-50, 135, 0};
        final double[] destinationPoint = new double[]{-2904698.555, 2904698.555, -4862789.038};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5201-27” for Geographic Geocentric transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5201-27</b></li>
     *   <li>Geocentric X (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>-2187336.719</b></li>
     *   <li>Geocentric Y (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813): <b>-112</b></li>
     *   <li>Geocentric Z (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); metre; EPSG CRS code 4813): <b>-5970149.093</b></li>
     *   <li>Latitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-70.00224647</b></li>
     *   <li>Longitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-179.9970662</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330): <b>-1039.29</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: Round Trip calculation point.
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5201-27 : FORWARD")
    public void GIGS_5201_27() throws TransformException {
        isRTC = true;
        isForward = true;
        final double[] originPoint = new double[]{-2187336.719, -112, -5970149.093};
        final double[] destinationPoint = new double[]{-70.00224647, -179.9970662, -1039.29};
        convertAndVerifyRoundTripPoint(originPoint, destinationPoint);
    }
}
