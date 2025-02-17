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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;


/**
 *  Verifies the software’s capabilities to perform transformations for Position Vector 7-parameter transformation method.
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
 *   <td><a href="https://github.com/IOGP-GIGS/GIGSTestDataset/blob/main/GIGSTestDatasetFiles/GIGS%205200%20Coordinate%20transformation%20test%20data/ASCII/GIGS_tfm_5203_PosVec_input_part1.txt">{@code GIGS_tfm_5203_PosVec_input_part1.txt}</a>
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
 * public class MyTest extends Test52031 {
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
public class Test52031 extends Series5000 {
    /**
     * Data about the CRS of the geographic CRS.
     *
     * @see #createGeogCRSA(TestMethod)
     */
    final Test3205 geogCRSATest;

    /**
     * Data about the CRS of the geographic CRS.
     *
     * @see #createGeogCRSB(TestMethod)
     */
    final Test3205 geogCRSBTest;

    /**
     * The geographic CRS created by this factory.
     */
    protected GeographicCRS geogCRSA;

    /**
     * The geographic CRS created by the factory,
     * or {@code null} if not yet created or if geographic CRS creation failed.
     */
    protected GeographicCRS geogCRSB;

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
    public Test52031(final Factories factories) throws FactoryException {
        super( 0.03, 0.0000003, 0.006, 0.00000006);
        copFactory = factories.copFactory;

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
     * Method used in class instantiation to initialise the geographic CRS B and the geographic CRS A
     * used in this test class.
     * <p>Geographic CRS A: GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277
     * <p>Geographic CRS B: GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326
     *
     * @throws FactoryException if an error occurred while creating a CRS.
     */
    @Override
    protected void createCRSs() throws FactoryException {
        createGeogCRSA(Test3205::GIGS_64005);
        createGeogCRSB(Test3205::GIGS_64003);
    }

    @Override
    protected void createCoOp() throws FactoryException {
        forwardCoOp = copFactory.createOperation(geogCRSA, geogCRSB);
        reverseCoOp = copFactory.createOperation(geogCRSB, geogCRSA);
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
    void createGeogCRSB(final TestMethod<Test3205> factory) throws FactoryException {
        factory.initialize(geogCRSBTest);
        geogCRSB = (GeographicCRS) geogCRSBTest.getIdentifiedObject();
    }

    /**
     * Verifies the result of the conversion produced by {@link #convertPoint(double[])} ()}.
     *
     * @param expectedPoint the expected coordinates from the GIGS output file.
     * @param resPoint the coordinates computed by the factory.
     */
    @Override
    void verifyConversion(double[] expectedPoint, double[] resPoint) {
        assertArrayEquals(expectedPoint, resPoint, isRTC ? rtGeoTolerance : geoTolerance);
    }

    /**
     * Tests “GIGS-5203-01” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-01</b></li>
     *   <li>Latitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>79.99487333</b></li>
     *   <li>Longitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>150.0056747</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>80</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>150</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: Round Trip calculation point.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5203-01 : REVERSE")
    public void GIGS_5203_01() throws TransformException {
        isRTC = true;
        isForward = false;
        final double[] originPoint = new double[]{80, 150};
        final double[] destinationPoint = new double[]{79.99487333, 150.0056747};
        convertAndVerifyRoundTripPoint(originPoint, destinationPoint);
    }

    /**
     * Tests “GIGS-5203-02” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-02</b></li>
     *   <li>Latitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>60</b></li>
     *   <li>Longitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>120</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>60.00569306</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>119.9943589</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5203-02 : FORWARD")
    public void GIGS_5203_02() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{60, 120};
        final double[] destinationPoint = new double[]{60.00569306, 119.9943589};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-03” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-03</b></li>
     *   <li>Latitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>29.99566778</b></li>
     *   <li>Longitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>60.00446778</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>30</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>60</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5203-03 : REVERSE")
    public void GIGS_5203_03() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{30, 60};
        final double[] destinationPoint = new double[]{29.99566778, 60.00446778};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-04” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-04</b></li>
     *   <li>Latitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>0</b></li>
     *   <li>Longitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>0</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>0.00483333</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-0.00089056</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5203-04 : FORWARD")
    public void GIGS_5203_04() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{0, 0};
        final double[] destinationPoint = new double[]{0.00483333, -0.00089056};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-05” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-05</b></li>
     *   <li>Latitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>-30.00504639</b></li>
     *   <li>Longitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>-60.00357056</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-30</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-60</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5203-05 : REVERSE")
    public void GIGS_5203_05() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-30, -60};
        final double[] destinationPoint = new double[]{-30.00504639, -60.00357056};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-06” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-06</b></li>
     *   <li>Latitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>-60</b></li>
     *   <li>Longitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>-120</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-59.99907361</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-119.9918525</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5203-06 : FORWARD")
    public void GIGS_5203_06() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-60, -120};
        final double[] destinationPoint = new double[]{-59.99907361, -119.9918525};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-07” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-07</b></li>
     *   <li>Latitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>-79.99778139</b></li>
     *   <li>Longitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>-150.0169311</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-80</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-150</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5203-07 : REVERSE")
    public void GIGS_5203_07() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-80, -150};
        final double[] destinationPoint = new double[]{-79.99778139, -150.0169311};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-08” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-08</b></li>
     *   <li>Latitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>-180</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>70.005945</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-179.9963736</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5203-08 : FORWARD")
    public void GIGS_5203_08() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{70, -180};
        final double[] destinationPoint = new double[]{70.005945, -179.9963736};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-09” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-09</b></li>
     *   <li>Latitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>49.99458694</b></li>
     *   <li>Longitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>-135.0059633</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>50</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-135</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5203-09 : REVERSE")
    public void GIGS_5203_09() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{50, -135};
        final double[] destinationPoint = new double[]{49.99458694, -135.0059633};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-10” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-10</b></li>
     *   <li>Latitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>25</b></li>
     *   <li>Longitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>-90</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>25.00445833</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-89.99531139</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5203-10 : FORWARD")
    public void GIGS_5203_10() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{25, -90};
        final double[] destinationPoint = new double[]{25.00445833, -89.99531139};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-11” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-11</b></li>
     *   <li>Latitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>-0.00483333</b></li>
     *   <li>Longitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>0.00089056</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>0</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>0</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5203-11 : REVERSE")
    public void GIGS_5203_11() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{0, 0};
        final double[] destinationPoint = new double[]{-0.00483333, 0.00089056};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-12” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-12</b></li>
     *   <li>Latitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>-37.6532236</b></li>
     *   <li>Longitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>143.9279419</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-37.65235306</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>143.9263481</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5203-12 : FORWARD")
    public void GIGS_5203_12() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-37.6532236, 143.9279419};
        final double[] destinationPoint = new double[]{-37.65235306, 143.9263481};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-13” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-13</b></li>
     *   <li>Latitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>-49.99973028</b></li>
     *   <li>Longitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>135.0029119</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-50</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>135</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5203-13 : REVERSE")
    public void GIGS_5203_13() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-50, 135};
        final double[] destinationPoint = new double[]{-49.99973028, 135.0029119};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5203-14” for PosVec transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5203-14</b></li>
     *   <li>Latitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>-70</b></li>
     *   <li>Longitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>180</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-70.002485</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-179.9966014</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5203-14 : FORWARD")
    public void GIGS_5203_14() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-70, 180};
        final double[] destinationPoint = new double[]{-70.002485, -179.9966014};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }
}
