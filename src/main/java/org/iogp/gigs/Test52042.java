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
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
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
 *  Verifies the software’s capabilities to perform transformations for Coordinate Frame Rotation method.
 *
 * <table class="gigs">
 * <caption>Test description</caption>
 * <tr>
 *   <th>Test method:</th>
 *   <td><ul>
 *      <li>Invoke GIGS transformation “GIGS geogCRS E to GIGS geogCRS A (2)”, GIGS code 15929 in both
 *      directions and inspect results, for all parts.</li>
 *      <li>For the first test point in part 1 and part 2, perform iterations of forward and reverse
 *      computations using the output of computation n as input into computation n+1, until the output
 *      coordinate values exceed more than 0.006m (6mm) or 0.00000006° from the original calculated
 *      values (but no more than 1000 iterations).</li></ul>
 *   </td>
 * </tr><tr>
 *   <th>Test data folder:</th>
 *   <td><a href="https://github.com/IOGP-GIGS/GIGSTestDataset/blob/main/GIGSTestDatasetFiles/GIGS%205200%20Coordinate%20transformation%20test%20data/ASCII/GIGS_tfm_5204_CoordFrame_input_part2.txt">{@code GIGS_tfm_5204_CoordFrame_input_part2.txt}</a>
 * </tr><tr>
 *   <th>Tested API:</th>
 *   <td>{@link CoordinateOperationFactory#createOperation(CoordinateReferenceSystem, CoordinateReferenceSystem)}.</td>
 * </tr><tr>
 *   <th>Expected result:</th>
 *   <td>Results should agree to within 0.03m (30mm) or 0.0000003° of the Test Data.
 *   <p>See file GIGS_tfm_5204_CoordFrame_output_part[x].
 *   <p>For the round trip calculation the initial calculated coordinates of the point should change by less
 *      than 0.006m (6mm) or 0.00000006° (before 1000 iterations).
 *   <p>Test result will be pass or fail. If fail, details of failure should be reported.
 *   <p>Some applications ignore ellipsoidal heights during these transformations (or set the input
 *      ellipsoidal heights to zero). If that is the case, results will match the results as shown for
 *      the geog2D case, EPSG method 9606. Horizontal coordinates obtained for those points with
 *      ellipsoidal heights significantly different from zero will be incorrect, whereas correct results may
 *      be generated for points with zero (or near zero) ellipsoidal heights. For large ellipsoidal heights
 *      (either positive or negative), the correct results are given by the geog3D EPSG method 1037.
 *   <p>Results that are a match should be clearly documented in the report on the test results.</td>
 * </tr></table>
 *
 *
 * <h2>Usage example</h2>
 * In order to specify their factories and run the tests in a JUnit framework, implementers can
 * define a subclass in their own test suite as in the example below:
 *
 * {@snippet lang = "java":
 * public class MyTest extends Test52042 {
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
@DisplayName("Coordinate Frame Rotation")
public class Test52042 extends Series5000 {
    /**
     * Data about the CRS of the geographic CRS.
     *
     * @see #createGeogCRSA(TestMethod)
     */
    final Test3205 geogCRSATest;

    /**
     * Data about the CRS of the geographic CRS.
     *
     * @see #createGeogCRSE(TestMethod)
     */
    final Test3205 geogCRSETest;

    /**
     * The geographic CRS created by this factory.
     */
    protected GeographicCRS geogCRSA;

    /**
     * The geographic CRS created by the factory,
     * or {@code null} if not yet created or if geographic CRS creation failed.
     */
    protected GeographicCRS geogCRSE;

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
    public Test52042(final Factories factories) throws FactoryException {
        super( 0.03, 0.0000003, 0.006, 0.00000006);
        copFactory = factories.copFactory;

        geogCRSATest = new Test3205(factories);
        geogCRSATest.skipTests = true;
        geogCRSATest.skipIdentificationCheck = true;

        geogCRSETest = new Test3205(factories);
        geogCRSETest.skipTests = true;
        geogCRSETest.skipIdentificationCheck = true;

        createCRSs();
        createCoOp();
    }

    /**
     * Method used in class instantiation to initialise the geographic CRS E and the geographic CRS A
     * used in this test class.
     * <p>Geographic CRS E: GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent
     * <p>Geographic CRS A: GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979
     *
     * @throws FactoryException if an error occurred while creating a CRS.
     */
    @Override
    protected void createCRSs() throws FactoryException {
        createGeogCRSE(Test3205::GIGS_64022);
        createGeogCRSA(Test3205::GIGS_64002);
    }

    @Override
    protected void createCoOp() throws FactoryException {
        forwardCoOp = copFactory.createOperation(geogCRSE, geogCRSA);
        reverseCoOp = copFactory.createOperation(geogCRSA, geogCRSE);
    }

    /**
     * Creates a user-defined geographic CRS by executing the specified method from the {@link Test3205} class.
     *
     * @param  factory          the test method to use for creating the geographic CRS.
     * @throws FactoryException if an error occurred while creating the geographic CRS.
     */
    void createGeogCRSA(final TestMethod<Test3205> factory) throws FactoryException {
        factory.initialize(geogCRSATest);
        geogCRSA = (GeographicCRS) geogCRSATest.getIdentifiedObject();
    }

    /**
     * Creates a user-defined geographic CRS by executing the specified method from the {@link Test3205} class.
     *
     * @param  factory          the test method to use for creating the geographic CRS.
     * @throws FactoryException if an error occurred while creating the geographic CRS.
     */
    void createGeogCRSE(final TestMethod<Test3205> factory) throws FactoryException {
        factory.initialize(geogCRSETest);
        geogCRSE = (GeographicCRS) geogCRSETest.getIdentifiedObject();
    }

    /**
     * Verifies the result of the conversion produced by {@link #convertPoint(double[])} ()}.
     *
     * @param expectedPoint the expected coordinates from the GIGS output file.
     * @param resPoint the coordinates computed by the factory.
     */
    @Override
    void verifyConversion(double[] expectedPoint, double[] resPoint) {
        assertEquals(3, resPoint.length);
        assertArrayEquals(Arrays.copyOf(expectedPoint, 2), Arrays.copyOf(resPoint, 2), isRTC ? rtGeoTolerance : geoTolerance);
        assertEquals(expectedPoint[2], resPoint[2], isRTC ? rtCartTolerance : cartTolerance);
    }

    /**
     * Tests “GIGS-5204-15” for CoordFrame transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5204-15</b></li>
     *   <li>Latitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>80.00155139</b></li>
     *   <li>Longitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>149.9982542</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; metre; No direct EPSG equivalent): <b>1141.395</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>80</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>150</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>1214.137</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: Round Trip calculation point.
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5204-15 : REVERSE")
    public void GIGS_5204_15() throws TransformException {
        isRTC = true;
        isForward = false;
        final double[] originPoint = new double[]{80, 150, 1214.137};
        final double[] destinationPoint = new double[]{80.00155139, 149.9982542, 1141.395};
        convertAndVerifyRoundTripPoint(originPoint, destinationPoint);
    }

    /**
     * Tests “GIGS-5204-16” for CoordFrame transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5204-16</b></li>
     *   <li>Latitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>80.00155167</b></li>
     *   <li>Longitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>149.9982539</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; metre; No direct EPSG equivalent): <b>-72.744</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>80</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>150</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>0</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5204-16 : REVERSE")
    public void GIGS_5204_16() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{80, 150, 0};
        final double[] destinationPoint = new double[]{80.00155167, 149.9982539, -72.744};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5204-17” for CoordFrame transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5204-17</b></li>
     *   <li>Latitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>60</b></li>
     *   <li>Longitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>120</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; metre; No direct EPSG equivalent): <b>900</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>59.99807361</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>120.0019711</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>1033.964</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5204-17 : FORWARD")
    public void GIGS_5204_17() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{60, 120, 900};
        final double[] destinationPoint = new double[]{59.99807361, 120.0019711, 1033.964};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5204-18” for CoordFrame transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5204-18</b></li>
     *   <li>Latitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>60</b></li>
     *   <li>Longitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>120</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>59.99807333</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>120.0019711</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>133.965</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5204-18 : FORWARD")
    public void GIGS_5204_18() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{60, 120, 0};
        final double[] destinationPoint = new double[]{59.99807333, 120.0019711, 133.965};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5204-19” for CoordFrame transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5204-19</b></li>
     *   <li>Latitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>30.00134083</b></li>
     *   <li>Longitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>59.99822194</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; metre; No direct EPSG equivalent): <b>28.351</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>30</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>60</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>189.569</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5204-19 : REVERSE")
    public void GIGS_5204_19() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{30, 60, 189.569};
        final double[] destinationPoint = new double[]{30.00134083, 59.99822194, 28.351};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5204-20” for CoordFrame transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5204-20</b></li>
     *   <li>Latitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>30.00134083</b></li>
     *   <li>Longitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>59.99822194</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; metre; No direct EPSG equivalent): <b>-161.219</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>30</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>60</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>0</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5204-20 : REVERSE")
    public void GIGS_5204_20() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{30, 60, 0};
        final double[] destinationPoint = new double[]{30.00134083, 59.99822194, -161.219};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5204-21” for CoordFrame transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5204-21</b></li>
     *   <li>Latitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Longitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-0.00081028</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>0.00098139</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>136</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5204-21 : FORWARD")
    public void GIGS_5204_21() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{0, 0, 0};
        final double[] destinationPoint = new double[]{-0.00081028, 0.00098139, 136};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5204-22” for CoordFrame transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5204-22</b></li>
     *   <li>Latitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Longitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; metre; No direct EPSG equivalent): <b>-3000</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-0.00081056</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>0.00098167</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-2863.996</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5204-22 : FORWARD")
    public void GIGS_5204_22() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{0, 0, -3000};
        final double[] destinationPoint = new double[]{-0.00081056, 0.00098167, -2863.996};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5204-23” for CoordFrame transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5204-23</b></li>
     *   <li>Latitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Longitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; metre; No direct EPSG equivalent): <b>-10000</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-0.00081167</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>0.00098222</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-9863.987</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5204-23 : FORWARD")
    public void GIGS_5204_23() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{0, 0, -10000};
        final double[] destinationPoint = new double[]{-0.00081167, 0.00098222, -9863.987};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5204-24” for CoordFrame transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5204-24</b></li>
     *   <li>Latitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>-29.99943917</b></li>
     *   <li>Longitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>-59.99991333</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; metre; No direct EPSG equivalent): <b>-186.458</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-30</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-60</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>0</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5204-24 : REVERSE")
    public void GIGS_5204_24() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-30, -60, 0};
        final double[] destinationPoint = new double[]{-29.99943917, -59.99991333, -186.458};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5204-25” for CoordFrame transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5204-25</b></li>
     *   <li>Latitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>-29.99943917</b></li>
     *   <li>Longitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>-59.99991333</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; metre; No direct EPSG equivalent): <b>-712.935</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-30</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-60</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-526.476</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5204-25 : REVERSE")
    public void GIGS_5204_25() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-30, -60, -526.476};
        final double[] destinationPoint = new double[]{-29.99943917, -59.99991333, -712.935};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5204-26” for CoordFrame transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5204-26</b></li>
     *   <li>Latitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>-29.99943917</b></li>
     *   <li>Longitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>-59.99991333</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; metre; No direct EPSG equivalent): <b>-757.935</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-30</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-60</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-571.476</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5204-26 : REVERSE")
    public void GIGS_5204_26() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-30, -60, -571.476};
        final double[] destinationPoint = new double[]{-29.99943917, -59.99991333, -757.935};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5204-27” for CoordFrame transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5204-27</b></li>
     *   <li>Latitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>-60</b></li>
     *   <li>Longitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>-120</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-59.999835</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-120.0015069</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>268.366</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5204-27 : FORWARD")
    public void GIGS_5204_27() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-60, -120, 0};
        final double[] destinationPoint = new double[]{-59.999835, -120.0015069, 268.366};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5204-28” for CoordFrame transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5204-28</b></li>
     *   <li>Latitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>-60</b></li>
     *   <li>Longitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>-120</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; metre; No direct EPSG equivalent): <b>-900</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-59.999835</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-120.0015072</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-631.633</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5204-28 : FORWARD")
    public void GIGS_5204_28() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-60, -120, -900};
        final double[] destinationPoint = new double[]{-59.999835, -120.0015072, -631.633};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5204-29” for CoordFrame transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5204-29</b></li>
     *   <li>Latitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>-80.00054917</b></li>
     *   <li>Longitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>-149.9953217</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; metre; No direct EPSG equivalent): <b>-267.988</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-80</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-150</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>0</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5204-29 : REVERSE")
    public void GIGS_5204_29() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-80, -150, 0};
        final double[] destinationPoint = new double[]{-80.00054917, -149.9953217, -267.988};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5204-30” for CoordFrame transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5204-30</b></li>
     *   <li>Latitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>-80.00054944</b></li>
     *   <li>Longitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>-149.9953208</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; metre; No direct EPSG equivalent): <b>-1239.244</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-80</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-150</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-971.255</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5204-30 : REVERSE")
    public void GIGS_5204_30() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-80, -150, -971.255};
        final double[] destinationPoint = new double[]{-80.00054944, -149.9953208, -1239.244};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5204-31” for CoordFrame transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5204-31</b></li>
     *   <li>Latitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>-80.00054944</b></li>
     *   <li>Longitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>-149.9953189</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; metre; No direct EPSG equivalent): <b>-3584.247</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-80</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-150</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-3316.255</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5204-31 : REVERSE")
    public void GIGS_5204_31() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-80, -150, -3316.255};
        final double[] destinationPoint = new double[]{-80.00054944, -149.9953189, -3584.247};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5204-32” for CoordFrame transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5204-32</b></li>
     *   <li>Latitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>-180</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>69.99812806</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>179.9993978</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>101.299</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5204-32 : FORWARD")
    public void GIGS_5204_32() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{70, -180, 0};
        final double[] destinationPoint = new double[]{69.99812806, 179.9993978, 101.299};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5204-33” for CoordFrame transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5204-33</b></li>
     *   <li>Latitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>50.00183</b></li>
     *   <li>Longitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>-134.9989139</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; metre; No direct EPSG equivalent): <b>-134.626</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>50</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-135</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>0</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5204-33 : REVERSE")
    public void GIGS_5204_33() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{50, -135, 0};
        final double[] destinationPoint = new double[]{50.00183, -134.9989139, -134.626};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5204-34” for CoordFrame transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5204-34</b></li>
     *   <li>Latitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>25</b></li>
     *   <li>Longitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>-90</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>24.99862667</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-90.00060583</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>135.34</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5204-34 : FORWARD")
    public void GIGS_5204_34() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{25, -90, 0};
        final double[] destinationPoint = new double[]{24.99862667, -90.00060583, 135.34};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5204-35” for CoordFrame transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5204-35</b></li>
     *   <li>Latitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>0.00081028</b></li>
     *   <li>Longitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>-0.00098139</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; metre; No direct EPSG equivalent): <b>-135.997</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>0</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>0</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>0</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5204-35 : REVERSE")
    public void GIGS_5204_35() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{0, 0, 0};
        final double[] destinationPoint = new double[]{0.00081028, -0.00098139, -135.997};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5204-36” for CoordFrame transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5204-36</b></li>
     *   <li>Latitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>-37.6532236</b></li>
     *   <li>Longitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>143.9279419</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; metre; No direct EPSG equivalent): <b>1836.947</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-37.65257111</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>143.9285722</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>2201.947</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5204-36 : FORWARD")
    public void GIGS_5204_36() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-37.6532236, 143.9279419, 1836.947};
        final double[] destinationPoint = new double[]{-37.65257111, 143.9285722, 2201.947};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5204-37” for CoordFrame transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5204-37</b></li>
     *   <li>Latitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>-37.6532236</b></li>
     *   <li>Longitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>143.9279419</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-37.65257083</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>143.9285722</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>365.002</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5204-37 : FORWARD")
    public void GIGS_5204_37() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-37.6532236, 143.9279419, 0};
        final double[] destinationPoint = new double[]{-37.65257083, 143.9285722, 365.002};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5204-38” for CoordFrame transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5204-38</b></li>
     *   <li>Latitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>-37.6532236</b></li>
     *   <li>Longitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>143.9279419</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; metre; No direct EPSG equivalent): <b>-3000</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-37.65257056</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>143.9285725</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-2634.994</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5204-38 : FORWARD")
    public void GIGS_5204_38() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-37.6532236, 143.9279419, -3000};
        final double[] destinationPoint = new double[]{-37.65257056, 143.9285725, -2634.994};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5204-39” for CoordFrame transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5204-39</b></li>
     *   <li>Latitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>-37.6532236</b></li>
     *   <li>Longitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>143.9279419</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; metre; No direct EPSG equivalent): <b>-10000</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-37.65256972</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>143.9285728</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-9634.985</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5204-39 : FORWARD")
    public void GIGS_5204_39() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-37.6532236, 143.9279419, -10000};
        final double[] destinationPoint = new double[]{-37.65256972, 143.9285728, -9634.985};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5204-40” for CoordFrame transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5204-40</b></li>
     *   <li>Latitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>-50.00096139</b></li>
     *   <li>Longitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>134.9991347</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; metre; No direct EPSG equivalent): <b>-341.15</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-50</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>135</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>0</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5204-40 : REVERSE")
    public void GIGS_5204_40() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-50, 135, 0};
        final double[] destinationPoint = new double[]{-50.00096139, 134.9991347, -341.15};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5204-41” for CoordFrame transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5204-41</b></li>
     *   <li>Latitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>-70</b></li>
     *   <li>Longitude (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; decimal degree; No direct EPSG equivalent): <b>180</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64022; GIGS geog3DCRS E; Belge 1972; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-69.99901667</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>179.9988867</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>296.295</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5204-41 : FORWARD")
    public void GIGS_5204_41() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-70, 180, 0};
        final double[] destinationPoint = new double[]{-69.99901667, 179.9988867, 296.295};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }
}
