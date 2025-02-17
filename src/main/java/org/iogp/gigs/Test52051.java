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
 *  Verifies the software’s capabilities to perform transformations for Molodensky-Badekas method.
 *  Either the Molodensky-Badekas (geog2D domain), method 9636 or Molodensky-Badekas (geog3D domain) method 1039 is acceptable.
 *
 * <table class="gigs">
 * <caption>Test description</caption>
 * <tr>
 *   <th>Test method:</th>
 *   <td><ul>
 *      <li>Invoke GIGS transformation “GIGS geogCRS C to GIGS geogCRS A (3)”, GIGS code 61003 in both
 *      directions and inspect results, for all parts.</li>
 *      <li>For the first test point in part 1 and part 2, perform iterations of forward and reverse
 *      computations using the output of computation n as input into computation n+1, until the output
 *      coordinate values exceed more than 0.00000006° from the original calculated values (but no more
 *      than 1000 iterations).
 *      </li></ul>
 *   </td>
 * </tr><tr>
 *   <th>Test data folder:</th>
 *   <td><a href="https://github.com/IOGP-GIGS/GIGSTestDataset/blob/main/GIGSTestDatasetFiles/GIGS%205200%20Coordinate%20transformation%20test%20data/ASCII/GIGS_tfm_5205_MolBad_input_part1.txt">{@code GIGS_tfm_5205_MolBad_input_part1.txt}</a>
 * </tr><tr>
 *   <th>Tested API:</th>
 *   <td>{@link CoordinateOperationFactory#createOperation(CoordinateReferenceSystem, CoordinateReferenceSystem)}.</td>
 * </tr><tr>
 *   <th>Expected result:</th>
 *   <td>Results should agree to within 0.03m (30mm) or 0.0000003° of the Test Data.
 *   <p>See file GIGS_tfm_5205_MolBad_output_part[x].
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
 * public class MyTest extends Test52051 {
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
@DisplayName("Molodensky-Badekas")
public class Test52051 extends Series5000 {
    /**
     * Data about the CRS of the geographic CRS.
     *
     * @see #createGeogCRSA(TestMethod)
     */
    final Test3205 geogCRSATest;

    /**
     * Data about the CRS of the geographic CRS.
     *
     * @see #createGeogCRSC(TestMethod)
     */
    final Test3205 geogCRSCTest;

    /**
     * The geographic CRS created by this factory.
     */
    protected GeographicCRS geogCRSA;

    /**
     * The geographic CRS created by the factory,
     * or {@code null} if not yet created or if geographic CRS creation failed.
     */
    protected GeographicCRS geogCRSC;

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
    public Test52051(final Factories factories) throws FactoryException {
        super( 0.03, 0.0000003, 0.006, 0.00000006);
        copFactory = factories.copFactory;

        geogCRSATest = new Test3205(factories);
        geogCRSATest.skipTests = true;
        geogCRSATest.skipIdentificationCheck = true;

        geogCRSCTest = new Test3205(factories);
        geogCRSCTest.skipTests = true;
        geogCRSCTest.skipIdentificationCheck = true;

        createCRSs();
        createCoOp();
    }

    /**
     * Method used in class instantiation to initialise the geographic CRS E and the geographic CRS A
     * used in this test class.
     * <p>Geographic CRS C: GIGS CRS Code 64006; GIGS geogCRS C; Amersfoort; decimal degree; EPSG CRS code 4289
     * <p>Geographic CRS A: GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326
     *
     * @throws FactoryException if an error occurred while creating a CRS.
     */
    @Override
    protected void createCRSs() throws FactoryException {
        createGeogCRSC(Test3205::GIGS_64006);
        createGeogCRSA(Test3205::GIGS_64003);
    }

    @Override
    protected void createCoOp() throws FactoryException {
        forwardCoOp = copFactory.createOperation(geogCRSC, geogCRSA);
        reverseCoOp = copFactory.createOperation(geogCRSA, geogCRSC);
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
    void createGeogCRSC(final TestMethod<Test3205> factory) throws FactoryException {
        factory.initialize(geogCRSCTest);
        geogCRSC = (GeographicCRS) geogCRSCTest.getIdentifiedObject();
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
     * Tests “GIGS-5205-01” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-01</b></li>
     *   <li>Latitude (GIGS CRS Code 64006; GIGS geogCRS C; Amersfoort; decimal degree; EPSG CRS code 4289): <b>79.99494639</b></li>
     *   <li>Longitude (GIGS CRS Code 64006; GIGS geogCRS C; Amersfoort; decimal degree; EPSG CRS code 4289): <b>150.0181437</b></li>
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
    @DisplayName("GIGS-5205-01 : REVERSE")
    public void GIGS_5205_01() throws TransformException {
        isRTC = true;
        isForward = false;
        final double[] originPoint = new double[]{80, 150};
        final double[] destinationPoint = new double[]{79.99494639, 150.0181437};
        convertAndVerifyRoundTripPoint(originPoint, destinationPoint);
    }

    /**
     * Tests “GIGS-5205-02” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-02</b></li>
     *   <li>Latitude (GIGS CRS Code 64006; GIGS geogCRS C; Amersfoort; decimal degree; EPSG CRS code 4289): <b>60</b></li>
     *   <li>Longitude (GIGS CRS Code 64006; GIGS geogCRS C; Amersfoort; decimal degree; EPSG CRS code 4289): <b>120</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>60.00441792</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>119.9900156</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5205-02 : FORWARD")
    public void GIGS_5205_02() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{60, 120};
        final double[] destinationPoint = new double[]{60.00441792, 119.9900156};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-03” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-03</b></li>
     *   <li>Latitude (GIGS CRS Code 64006; GIGS geogCRS C; Amersfoort; decimal degree; EPSG CRS code 4289): <b>29.99746103</b></li>
     *   <li>Longitude (GIGS CRS Code 64006; GIGS geogCRS C; Amersfoort; decimal degree; EPSG CRS code 4289): <b>60.00535007</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>30</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>60</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5205-03 : REVERSE")
    public void GIGS_5205_03() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{30, 60};
        final double[] destinationPoint = new double[]{29.99746103, 60.00535007};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-04” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-04</b></li>
     *   <li>Latitude (GIGS CRS Code 64006; GIGS geogCRS C; Amersfoort; decimal degree; EPSG CRS code 4289): <b>0</b></li>
     *   <li>Longitude (GIGS CRS Code 64006; GIGS geogCRS C; Amersfoort; decimal degree; EPSG CRS code 4289): <b>0</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>0.0041133</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-0.00007025</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5205-04 : FORWARD")
    public void GIGS_5205_04() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{0, 0};
        final double[] destinationPoint = new double[]{0.0041133, -0.00007025};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-05” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-05</b></li>
     *   <li>Latitude (GIGS CRS Code 64006; GIGS geogCRS C; Amersfoort; decimal degree; EPSG CRS code 4289): <b>-30.0042486</b></li>
     *   <li>Longitude (GIGS CRS Code 64006; GIGS geogCRS C; Amersfoort; decimal degree; EPSG CRS code 4289): <b>-60.00473241</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-30</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-60</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5205-05 : REVERSE")
    public void GIGS_5205_05() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-30, -60};
        final double[] destinationPoint = new double[]{-30.0042486, -60.00473241};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-06” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-06</b></li>
     *   <li>Latitude (GIGS CRS Code 64006; GIGS geogCRS C; Amersfoort; decimal degree; EPSG CRS code 4289): <b>-60</b></li>
     *   <li>Longitude (GIGS CRS Code 64006; GIGS geogCRS C; Amersfoort; decimal degree; EPSG CRS code 4289): <b>-120</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-60.00081456</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-119.9922412</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5205-06 : FORWARD")
    public void GIGS_5205_06() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-60, -120};
        final double[] destinationPoint = new double[]{-60.00081456, -119.9922412};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-07” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-07</b></li>
     *   <li>Latitude (GIGS CRS Code 64006; GIGS geogCRS C; Amersfoort; decimal degree; EPSG CRS code 4289): <b>-79.99612229</b></li>
     *   <li>Longitude (GIGS CRS Code 64006; GIGS geogCRS C; Amersfoort; decimal degree; EPSG CRS code 4289): <b>-150.0120919</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-80</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-150</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5205-07 : REVERSE")
    public void GIGS_5205_07() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-80, -150};
        final double[] destinationPoint = new double[]{-79.99612229, -150.0120919};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-08” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-08</b></li>
     *   <li>Latitude (GIGS CRS Code 64006; GIGS geogCRS C; Amersfoort; decimal degree; EPSG CRS code 4289): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64006; GIGS geogCRS C; Amersfoort; decimal degree; EPSG CRS code 4289): <b>-180</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>70.00666951</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>179.9978617</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5205-08 : FORWARD")
    public void GIGS_5205_08() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{70, -180};
        final double[] destinationPoint = new double[]{70.00666951, 179.9978617};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-09” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-09</b></li>
     *   <li>Latitude (GIGS CRS Code 64006; GIGS geogCRS C; Amersfoort; decimal degree; EPSG CRS code 4289): <b>49.99357566</b></li>
     *   <li>Longitude (GIGS CRS Code 64006; GIGS geogCRS C; Amersfoort; decimal degree; EPSG CRS code 4289): <b>-135.0045485</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>50</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-135</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5205-09 : REVERSE")
    public void GIGS_5205_09() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{50, -135};
        final double[] destinationPoint = new double[]{49.99357566, -135.0045485};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-10” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-10</b></li>
     *   <li>Latitude (GIGS CRS Code 64006; GIGS geogCRS C; Amersfoort; decimal degree; EPSG CRS code 4289): <b>25</b></li>
     *   <li>Longitude (GIGS CRS Code 64006; GIGS geogCRS C; Amersfoort; decimal degree; EPSG CRS code 4289): <b>-90</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>25.00457271</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-89.99487454</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5205-10 : FORWARD")
    public void GIGS_5205_10() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{25, -90};
        final double[] destinationPoint = new double[]{25.00457271, -89.99487454};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-11” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-11</b></li>
     *   <li>Latitude (GIGS CRS Code 64006; GIGS geogCRS C; Amersfoort; decimal degree; EPSG CRS code 4289): <b>-0.00411349</b></li>
     *   <li>Longitude (GIGS CRS Code 64006; GIGS geogCRS C; Amersfoort; decimal degree; EPSG CRS code 4289): <b>0.00007032</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>0</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>0</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5205-11 : REVERSE")
    public void GIGS_5205_11() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{0, 0};
        final double[] destinationPoint = new double[]{-0.00411349, 0.00007032};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-12” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-12</b></li>
     *   <li>Latitude (GIGS CRS Code 64006; GIGS geogCRS C; Amersfoort; decimal degree; EPSG CRS code 4289): <b>-37.6532236</b></li>
     *   <li>Longitude (GIGS CRS Code 64006; GIGS geogCRS C; Amersfoort; decimal degree; EPSG CRS code 4289): <b>143.9279419</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-37.65282034</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>143.923306</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5205-12 : FORWARD")
    public void GIGS_5205_12() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-37.6532236, 143.9279419};
        final double[] destinationPoint = new double[]{-37.65282034, 143.923306};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-13” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-13</b></li>
     *   <li>Latitude (GIGS CRS Code 64006; GIGS geogCRS C; Amersfoort; decimal degree; EPSG CRS code 4289): <b>-49.99958243</b></li>
     *   <li>Longitude (GIGS CRS Code 64006; GIGS geogCRS C; Amersfoort; decimal degree; EPSG CRS code 4289): <b>135.0064107</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-50</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>135</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5205-13 : REVERSE")
    public void GIGS_5205_13() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-50, 135};
        final double[] destinationPoint = new double[]{-49.99958243, 135.0064107};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-14” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-14</b></li>
     *   <li>Latitude (GIGS CRS Code 64006; GIGS geogCRS C; Amersfoort; decimal degree; EPSG CRS code 4289): <b>-70</b></li>
     *   <li>Longitude (GIGS CRS Code 64006; GIGS geogCRS C; Amersfoort; decimal degree; EPSG CRS code 4289): <b>180</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-70.00362084</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>179.9984789</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5205-14 : FORWARD")
    public void GIGS_5205_14() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-70, 180};
        final double[] destinationPoint = new double[]{-70.00362084, 179.9984789};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }
}
