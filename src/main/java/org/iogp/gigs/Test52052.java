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
 *  Verifies the software’s capabilities to perform transformations for MMolodensky-Badekas method.
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
 *   <td><a href="https://github.com/IOGP-GIGS/GIGSTestDataset/blob/main/GIGSTestDatasetFiles/GIGS%205200%20Coordinate%20transformation%20test%20data/ASCII/GIGS_tfm_5205_MolBad_input_part2.txt">{@code GIGS_tfm_5205_MolBad_input_part2.txt}</a>
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
 * public class MyTest extends Test52052 {
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
@DisplayName("MMolodensky-Badekas")
public class Test52052 extends Series5000 {
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
    public Test52052(final Factories factories) throws FactoryException {
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
     * <p>Geographic CRS C: GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent
     * <p>Geographic CRS A: GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979
     *
     * @throws FactoryException if an error occurred while creating a CRS.
     */
    @Override
    protected void createCRSs() throws FactoryException {
        createGeogCRSC(Test3205::GIGS_64021);
        createGeogCRSA(Test3205::GIGS_64002);
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
        assertEquals(3, resPoint.length);
        assertArrayEquals(Arrays.copyOf(expectedPoint, 2), Arrays.copyOf(resPoint, 2), isRTC ? rtGeoTolerance : geoTolerance);
        assertEquals(expectedPoint[2], resPoint[2], isRTC ? rtCartTolerance : cartTolerance);
    }

    /**
     * Tests “GIGS-5205-15” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-15</b></li>
     *   <li>Latitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>79.99494735</b></li>
     *   <li>Longitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>150.0181405</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; metre; No direct EPSG equivalent): <b>1485.658</b></li>
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
    @DisplayName("GIGS-5205-15 : REVERSE")
    public void GIGS_5205_15() throws TransformException {
        isRTC = true;
        isForward = false;
        final double[] originPoint = new double[]{80, 150, 1214.137};
        final double[] destinationPoint = new double[]{79.99494735, 150.0181405, 1485.658};
        convertAndVerifyRoundTripPoint(originPoint, destinationPoint);
    }

    /**
     * Tests “GIGS-5205-16” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-16</b></li>
     *   <li>Latitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>79.99494639</b></li>
     *   <li>Longitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>150.0181437</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; metre; No direct EPSG equivalent): <b>271.526</b></li>
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
    @DisplayName("GIGS-5205-16 : REVERSE")
    public void GIGS_5205_16() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{80, 150, 0};
        final double[] destinationPoint = new double[]{79.99494639, 150.0181437, 271.526};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-17” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-17</b></li>
     *   <li>Latitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>60</b></li>
     *   <li>Longitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>120</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; metre; No direct EPSG equivalent): <b>900</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>60.00441729</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>119.9900169</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>519.593</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5205-17 : FORWARD")
    public void GIGS_5205_17() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{60, 120, 900};
        final double[] destinationPoint = new double[]{60.00441729, 119.9900169, 519.593};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-18” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-18</b></li>
     *   <li>Latitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>60</b></li>
     *   <li>Longitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>120</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>60.00441792</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>119.9900156</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-380.411</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5205-18 : FORWARD")
    public void GIGS_5205_18() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{60, 120, 0};
        final double[] destinationPoint = new double[]{60.00441792, 119.9900156, -380.411};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-19” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-19</b></li>
     *   <li>Latitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>29.99746111</b></li>
     *   <li>Longitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>60.00534993</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; metre; No direct EPSG equivalent): <b>371.808</b></li>
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
    @DisplayName("GIGS-5205-19 : REVERSE")
    public void GIGS_5205_19() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{30, 60, 189.569};
        final double[] destinationPoint = new double[]{29.99746111, 60.00534993, 371.808};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-20” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-20</b></li>
     *   <li>Latitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>29.99746103</b></li>
     *   <li>Longitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>60.00535007</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; metre; No direct EPSG equivalent): <b>182.24</b></li>
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
    @DisplayName("GIGS-5205-20 : REVERSE")
    public void GIGS_5205_20() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{30, 60, 0};
        final double[] destinationPoint = new double[]{29.99746103, 60.00535007, 182.24};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-21” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-21</b></li>
     *   <li>Latitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Longitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>0.0041133</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-0.00007025</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-148.564</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5205-21 : FORWARD")
    public void GIGS_5205_21() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{0, 0, 0};
        final double[] destinationPoint = new double[]{0.0041133, -0.00007025, -148.564};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-22” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-22</b></li>
     *   <li>Latitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Longitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; metre; No direct EPSG equivalent): <b>-3000</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>0.00411529</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-0.00007004</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-3148.576</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5205-22 : FORWARD")
    public void GIGS_5205_22() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{0, 0, -3000};
        final double[] destinationPoint = new double[]{0.00411529, -0.00007004, -3148.576};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-23” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-23</b></li>
     *   <li>Latitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Longitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; metre; No direct EPSG equivalent): <b>-10000</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>0.00411996</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-0.00006955</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-10148.604</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5205-23 : FORWARD")
    public void GIGS_5205_23() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{0, 0, -10000};
        final double[] destinationPoint = new double[]{0.00411996, -0.00006955, -10148.604};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-24” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-24</b></li>
     *   <li>Latitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>-30.0042486</b></li>
     *   <li>Longitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>-60.00473241</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; metre; No direct EPSG equivalent): <b>722.882</b></li>
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
    @DisplayName("GIGS-5205-24 : REVERSE")
    public void GIGS_5205_24() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-30, -60, 0};
        final double[] destinationPoint = new double[]{-30.0042486, -60.00473241, 722.882};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-25” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-25</b></li>
     *   <li>Latitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>-30.00424895</b></li>
     *   <li>Longitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>-60.00473285</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; metre; No direct EPSG equivalent): <b>196.408</b></li>
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
    @DisplayName("GIGS-5205-25 : REVERSE")
    public void GIGS_5205_25() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-30, -60, -526.476};
        final double[] destinationPoint = new double[]{-30.00424895, -60.00473285, 196.408};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-26” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-26</b></li>
     *   <li>Latitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>-30.00424898</b></li>
     *   <li>Longitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>-60.00473289</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; metre; No direct EPSG equivalent): <b>151.409</b></li>
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
    @DisplayName("GIGS-5205-26 : REVERSE")
    public void GIGS_5205_26() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-30, -60, -571.476};
        final double[] destinationPoint = new double[]{-30.00424898, -60.00473289, 151.409};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-27” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-27</b></li>
     *   <li>Latitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>-60</b></li>
     *   <li>Longitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>-120</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-60.00081456</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-119.9922412</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-1230.32</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5205-27 : FORWARD")
    public void GIGS_5205_27() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-60, -120, 0};
        final double[] destinationPoint = new double[]{-60.00081456, -119.9922412, -1230.32};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-28” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-28</b></li>
     *   <li>Latitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>-60</b></li>
     *   <li>Longitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>-120</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; metre; No direct EPSG equivalent): <b>-900</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-60.0008147</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-119.99224</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-2130.324</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5205-28 : FORWARD")
    public void GIGS_5205_28() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-60, -120, -900};
        final double[] destinationPoint = new double[]{-60.0008147, -119.99224, -2130.324};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-29” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-29</b></li>
     *   <li>Latitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>-79.99612229</b></li>
     *   <li>Longitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>-150.0120919</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; metre; No direct EPSG equivalent): <b>1197.381</b></li>
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
    @DisplayName("GIGS-5205-29 : REVERSE")
    public void GIGS_5205_29() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-80, -150, 0};
        final double[] destinationPoint = new double[]{-79.99612229, -150.0120919, 1197.381};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-30” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-30</b></li>
     *   <li>Latitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>-79.99612168</b></li>
     *   <li>Longitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>-150.0120938</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; metre; No direct EPSG equivalent): <b>226.13</b></li>
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
    @DisplayName("GIGS-5205-30 : REVERSE")
    public void GIGS_5205_30() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-80, -150, -971.255};
        final double[] destinationPoint = new double[]{-79.99612168, -150.0120938, 226.13};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-31” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-31</b></li>
     *   <li>Latitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>-79.9961202</b></li>
     *   <li>Longitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>-150.0120983</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; metre; No direct EPSG equivalent): <b>-2118.86</b></li>
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
    @DisplayName("GIGS-5205-31 : REVERSE")
    public void GIGS_5205_31() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-80, -150, -3316.255};
        final double[] destinationPoint = new double[]{-79.9961202, -150.0120983, -2118.86};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-32” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-32</b></li>
     *   <li>Latitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>-180</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>70.00666951</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>179.9978617</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-410.882</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5205-32 : FORWARD")
    public void GIGS_5205_32() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{70, -180, 0};
        final double[] destinationPoint = new double[]{70.00666951, 179.9978617, -410.882};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-33” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-33</b></li>
     *   <li>Latitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>49.99357566</b></li>
     *   <li>Longitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>-135.0045485</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; metre; No direct EPSG equivalent): <b>597.819</b></li>
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
    @DisplayName("GIGS-5205-33 : REVERSE")
    public void GIGS_5205_33() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{50, -135, 0};
        final double[] destinationPoint = new double[]{49.99357566, -135.0045485, 597.819};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-34” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-34</b></li>
     *   <li>Latitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>25</b></li>
     *   <li>Longitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>-90</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>25.00457271</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-89.99487454</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-550.442</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5205-34 : FORWARD")
    public void GIGS_5205_34() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{25, -90, 0};
        final double[] destinationPoint = new double[]{25.00457271, -89.99487454, -550.442};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-35” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-35</b></li>
     *   <li>Latitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>-0.00411349</b></li>
     *   <li>Longitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>0.00007032</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; metre; No direct EPSG equivalent): <b>148.593</b></li>
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
    @DisplayName("GIGS-5205-35 : REVERSE")
    public void GIGS_5205_35() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{0, 0, 0};
        final double[] destinationPoint = new double[]{-0.00411349, 0.00007032, 148.593};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-36” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-36</b></li>
     *   <li>Latitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>-37.6532236</b></li>
     *   <li>Longitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>143.9279419</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; metre; No direct EPSG equivalent): <b>1836.947</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-37.65282045</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>143.9233072</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>525.013</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5205-36 : FORWARD")
    public void GIGS_5205_36() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-37.6532236, 143.9279419, 1836.947};
        final double[] destinationPoint = new double[]{-37.65282045, 143.9233072, 525.013};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-37” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-37</b></li>
     *   <li>Latitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>-37.6532236</b></li>
     *   <li>Longitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>143.9279419</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-37.65282034</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>143.923306</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-1311.941</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5205-37 : FORWARD")
    public void GIGS_5205_37() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-37.6532236, 143.9279419, 0};
        final double[] destinationPoint = new double[]{-37.65282034, 143.923306, -1311.941};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-38” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-38</b></li>
     *   <li>Latitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>-37.6532236</b></li>
     *   <li>Longitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>143.9279419</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; metre; No direct EPSG equivalent): <b>-3000</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-37.65282015</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>143.923304</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-4311.954</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5205-38 : FORWARD")
    public void GIGS_5205_38() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-37.6532236, 143.9279419, -3000};
        final double[] destinationPoint = new double[]{-37.65282015, 143.923304, -4311.954};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-39” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-39</b></li>
     *   <li>Latitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>-37.6532236</b></li>
     *   <li>Longitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>143.9279419</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; metre; No direct EPSG equivalent): <b>-10000</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-37.65281972</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>143.9232994</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-11311.982</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5205-39 : FORWARD")
    public void GIGS_5205_39() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-37.6532236, 143.9279419, -10000};
        final double[] destinationPoint = new double[]{-37.65281972, 143.9232994, -11311.982};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-40” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-40</b></li>
     *   <li>Latitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>-49.99958243</b></li>
     *   <li>Longitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>135.0064107</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; metre; No direct EPSG equivalent): <b>1265.808</b></li>
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
    @DisplayName("GIGS-5205-40 : REVERSE")
    public void GIGS_5205_40() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-50, 135, 0};
        final double[] destinationPoint = new double[]{-49.99958243, 135.0064107, 1265.808};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5205-41” for MolBad transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5205-41</b></li>
     *   <li>Latitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>-70</b></li>
     *   <li>Longitude (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; decimal degree; No direct EPSG equivalent): <b>180</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64021; GIGS geog3DCRS C; Amersfoort; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-70.00362084</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>179.9984789</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-1286.11</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5205-41 : FORWARD")
    public void GIGS_5205_41() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-70, 180, 0};
        final double[] destinationPoint = new double[]{-70.00362084, 179.9984789, -1286.11};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }
}
