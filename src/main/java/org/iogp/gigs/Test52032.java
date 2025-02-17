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
 * Verifies the software’s capabilities to perform transformations for Position Vector 7-parameter transformation method.
 *
 * <table class="gigs">
 * <caption>Test description</caption>
 * <tr>
 *   <th>Test method:</th>
 *   <td><ul>
 *      <li>Invoke GIGS transformation “GIGS geogCRS B to GIGS geogCRS A (2)”, GIGS code 61314 in both
 *      directions and inspect results, for all parts.</li>
 *      <li>For the first test point in part 1 and part 2, perform iterations of forward and reverse
 *      computations using the output of computation n as input into computation n+1, until the output
 *      coordinate values exceed more than 0.006m (6mm) or 0.00000006° from the original calculated
 *      values (but no more than 1000 iterations).</li></ul>
 *   </td>
 * </tr><tr>
 *   <th>Test data folder:</th>
 *   <td><a href="https://github.com/IOGP-GIGS/GIGSTestDataset/blob/main/GIGSTestDatasetFiles/GIGS%205200%20Coordinate%20transformation%20test%20data/ASCII/GIGS_tfm_5203_PosVec_input_part2.txt">{@code GIGS_tfm_5203_PosVec_input_part2.txt}</a>
 * </tr><tr>
 *   <th>Tested API:</th>
 *   <td>{@link CoordinateOperationFactory#createOperation(CoordinateReferenceSystem, CoordinateReferenceSystem)}.</td>
 * </tr><tr>
 *   <th>Expected result:</th>
 *   <td>Results should agree to within 0.03m (30mm) or 0.0000003° of the Test Data.
 *   <p>See file GIGS_tfm_5203_PosVec_output_part[x].
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
@DisplayName("Position Vector 7-parameter transformation")
public class Test52032 extends Series5000 {
    /**
     * Data about the CRS of the geographic CRS.
     *
     * @see #createGeogCRSB(TestMethod)
     */
    final Test3205 geogCRSBTest;

    /**
     * Data about the CRS of the geographic CRS.
     *
     * @see #createGeogCRSA(TestMethod)
     */
    final Test3205 geogCRSATest;

    /**
     * The geographic CRS created by this factory.
     */
    protected GeographicCRS geogCRSB;

    /**
     * The geographic CRS created by the factory,
     * or {@code null} if not yet created or if geocentric CRS creation failed.
     */
    protected GeographicCRS geogCRSA;

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
    public Test52032(final Factories factories) throws FactoryException {
        super( 0.03, 0.0000003, 0.006, 0.00000006);
        copFactory            = factories.copFactory;

        geogCRSBTest = new Test3205(factories);
        geogCRSBTest.skipTests = true;
        geogCRSBTest.skipIdentificationCheck = true;

        geogCRSATest = new Test3205(factories);
        geogCRSATest.skipTests = true;
        geogCRSATest.skipIdentificationCheck = true;
        createCRSs();
        createCoOp();
    }

    /**
     * Method used in class instantiation to initialise the geocentric CRS and the geographic CRS
     * used in this test class.
     * <p>GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent
     * <p>Geographic CRS : GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979
     *
     * @throws FactoryException if an error occurred while creating a CRS.
     */
    @Override
    protected void createCRSs() throws FactoryException {
        createGeogCRSB(Test3205::GIGS_64019);
        createGeogCRSA(Test3205::GIGS_64002);
    }

    @Override
    protected void createCoOp() throws FactoryException {
        forwardCoOp = copFactory.createOperation(geogCRSB, geogCRSA);
        reverseCoOp = copFactory.createOperation(geogCRSA, geogCRSB);
    }

    /**
     * Creates a user-defined geographic CRS by executing the specified method from the {@link Test3205} class.
     *
     * @param  factory          the test method to use for creating the geographic CRS.
     * @throws FactoryException if an error occurred while creating the geographic CRS.
     */
    void createGeogCRSB(final TestMethod<Test3205> factory) throws FactoryException {
        factory.initialize(geogCRSBTest);
        geogCRSB = (GeographicCRS) geogCRSBTest.getIdentifiedObject();
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
     * Verifies the result of the conversion produced by {@link #convertPoint(double[])} ()}.
     *
     * @param expectedPoint the expected coordinates from the GIGS output file.
     * @param resPoint the coordinates computed by the factory.
     */
    void verifyConversion(double[] expectedPoint, double[] resPoint) {
        assertEquals(3, resPoint.length);
        assertArrayEquals(Arrays.copyOf(expectedPoint, 2), Arrays.copyOf(resPoint, 2), isRTC ? rtGeoTolerance : geoTolerance);
        assertEquals(expectedPoint[2], resPoint[2], isRTC ? rtCartTolerance : cartTolerance);
    }

    /**
     * Tests “GIGS-5203-15” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-15</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>79.99487417</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>150.0056736</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>1386.388</b></li>
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
    @DisplayName("GIGS-5203-15 : REVERSE")
    public void GIGS_5203_15() throws TransformException {
        isRTC = true;
        isForward = false;
        final double[] originPoint = new double[]{80, 150, 1214.137};
        final double[] destinationPoint = new double[]{79.99487417, 150.0056736, 1386.388};
        convertAndVerifyRoundTripPoint(originPoint, destinationPoint);
    }

    /**
     * Tests “GIGS-5203-16” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-16</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>79.99487333</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>150.0056747</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>172.226</b></li>
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
    @DisplayName("GIGS-5203-16 : REVERSE")
    public void GIGS_5203_16() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{80, 150, 0};
        final double[] destinationPoint = new double[]{79.99487333, 150.0056747, 172.226};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-17” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-17</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>60</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>120</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>900</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>60.00569222</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>119.9943597</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>558.326</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5203-17 : FORWARD")
    public void GIGS_5203_17() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{60, 120, 900};
        final double[] destinationPoint = new double[]{60.00569222, 119.9943597, 558.326};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-18” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-18</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>60</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>120</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>60.00569306</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>119.9943589</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-341.655</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5203-18 : FORWARD")
    public void GIGS_5203_18() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{60, 120, 0};
        final double[] destinationPoint = new double[]{60.00569306, 119.9943589, -341.655};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-19” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-19</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>29.99566806</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>60.00446778</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>503.803</b></li>
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
    @DisplayName("GIGS-5203-19 : REVERSE")
    public void GIGS_5203_19() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{30, 60, 189.569};
        final double[] destinationPoint = new double[]{29.99566806, 60.00446778, 503.803};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-20” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-20</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>29.99566778</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>60.00446778</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>314.23</b></li>
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
    @DisplayName("GIGS-5203-20 : REVERSE")
    public void GIGS_5203_20() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{30, 60, 0};
        final double[] destinationPoint = new double[]{29.99566778, 60.00446778, 314.23};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-21” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-21</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>0.00483333</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-0.00089056</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-257.805</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5203-21 : FORWARD")
    public void GIGS_5203_21() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{0, 0, 0};
        final double[] destinationPoint = new double[]{0.00483333, -0.00089056, -257.805};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-22” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-22</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-3000</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>0.00483556</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-0.00089111</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-3257.744</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5203-22 : FORWARD")
    public void GIGS_5203_22() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{0, 0, -3000};
        final double[] destinationPoint = new double[]{0.00483556, -0.00089111, -3257.744};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-23” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-23</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-10000</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>0.00484111</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-0.00089222</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-10257.6</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5203-23 : FORWARD")
    public void GIGS_5203_23() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{0, 0, -10000};
        final double[] destinationPoint = new double[]{0.00484111, -0.00089222, -10257.6};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-24” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-24</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-30.00504639</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-60.00357056</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>668.533</b></li>
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
    @DisplayName("GIGS-5203-24 : REVERSE")
    public void GIGS_5203_24() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-30, -60, 0};
        final double[] destinationPoint = new double[]{-30.00504639, -60.00357056, 668.533};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-25” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-25</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-30.00504667</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-60.00357083</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>142.047</b></li>
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
    @DisplayName("GIGS-5203-25 : REVERSE")
    public void GIGS_5203_25() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-30, -60, -526.476};
        final double[] destinationPoint = new double[]{-30.00504667, -60.00357083, 142.047};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-26” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-26</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-30.00504667</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-60.00357083</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>97.046</b></li>
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
    @DisplayName("GIGS-5203-26 : REVERSE")
    public void GIGS_5203_26() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-30, -60, -571.476};
        final double[] destinationPoint = new double[]{-30.00504667, -60.00357083, 97.046};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-27” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-27</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-60</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-120</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-59.99907361</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-119.9918525</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-1172.186</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5203-27 : FORWARD")
    public void GIGS_5203_27() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-60, -120, 0};
        final double[] destinationPoint = new double[]{-59.99907361, -119.9918525, -1172.186};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-28” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-28</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-60</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-120</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-900</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-59.99907361</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-119.9918514</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-2072.168</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5203-28 : FORWARD")
    public void GIGS_5203_28() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-60, -120, -900};
        final double[] destinationPoint = new double[]{-59.99907361, -119.9918514, -2072.168};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-29” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-29</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-79.99778139</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-150.0169311</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>1218.145</b></li>
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
    @DisplayName("GIGS-5203-29 : REVERSE")
    public void GIGS_5203_29() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-80, -150, 0};
        final double[] destinationPoint = new double[]{-79.99778139, -150.0169311, 1218.145};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-30” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-30</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-79.99778111</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-150.0169339</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>246.87</b></li>
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
    @DisplayName("GIGS-5203-30 : REVERSE")
    public void GIGS_5203_30() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-80, -150, -971.255};
        final double[] destinationPoint = new double[]{-79.99778111, -150.0169339, 246.87};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-31” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-31</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-79.99778028</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-150.01694</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-2098.178</b></li>
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
    @DisplayName("GIGS-5203-31 : REVERSE")
    public void GIGS_5203_31() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-80, -150, -3316.255};
        final double[] destinationPoint = new double[]{-79.99778028, -150.01694, -2098.178};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-32” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-32</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-180</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>70.005945</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-179.9963736</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-278.139</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5203-32 : FORWARD")
    public void GIGS_5203_32() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{70, -180, 0};
        final double[] destinationPoint = new double[]{70.005945, -179.9963736, -278.139};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-33” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-33</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>49.99458694</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-135.0059633</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>389.032</b></li>
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
    @DisplayName("GIGS-5203-33 : REVERSE")
    public void GIGS_5203_33() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{50, -135, 0};
        final double[] destinationPoint = new double[]{49.99458694, -135.0059633, 389.032};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-34” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-34</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>25</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-90</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>25.00445833</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-89.99531139</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-347.73</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5203-34 : FORWARD")
    public void GIGS_5203_34() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{25, -90, 0};
        final double[] destinationPoint = new double[]{25.00445833, -89.99531139, -347.73};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-35” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-35</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-0.00483333</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>0.00089056</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>257.864</b></li>
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
    @DisplayName("GIGS-5203-35 : REVERSE")
    public void GIGS_5203_35() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{0, 0, 0};
        final double[] destinationPoint = new double[]{-0.00483333, 0.00089056, 257.864};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-36” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-36</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-37.6532236</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>143.9279419</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>1836.947</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-37.65235333</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>143.9263486</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>486.716</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5203-36 : FORWARD")
    public void GIGS_5203_36() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-37.6532236, 143.9279419, 1836.947};
        final double[] destinationPoint = new double[]{-37.65235333, 143.9263486, 486.716};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-37” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-37</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-37.6532236</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>143.9279419</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-37.65235306</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>143.9263481</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-1350.193</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5203-37 : FORWARD")
    public void GIGS_5203_37() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-37.6532236, 143.9279419, 0};
        final double[] destinationPoint = new double[]{-37.65235306, 143.9263481, -1350.193};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-38” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-38</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-37.6532236</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>143.9279419</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-3000</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-37.65235278</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>143.9263472</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-4350.132</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5203-38 : FORWARD")
    public void GIGS_5203_38() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-37.6532236, 143.9279419, -3000};
        final double[] destinationPoint = new double[]{-37.65235278, 143.9263472, -4350.132};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-39” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-39</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-37.6532236</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>143.9279419</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-10000</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-37.65235167</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>143.9263453</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-11349.988</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5203-39 : FORWARD")
    public void GIGS_5203_39() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-37.6532236, 143.9279419, -10000};
        final double[] destinationPoint = new double[]{-37.65235167, 143.9263453, -11349.988};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-40” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-40</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-49.99973028</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>135.0029119</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>1333.285</b></li>
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
    @DisplayName("GIGS-5203-40 : REVERSE")
    public void GIGS_5203_40() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-50, 135, 0};
        final double[] destinationPoint = new double[]{-49.99973028, 135.0029119, 1333.285};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-41” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-41</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-70</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>180</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-70.002485</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-179.9966014</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-1296.939</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5203-41 : FORWARD")
    public void GIGS_5203_41() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-70, 180, 0};
        final double[] destinationPoint = new double[]{-70.002485, -179.9966014, -1296.939};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }
}
