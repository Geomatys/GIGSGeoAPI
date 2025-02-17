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
 *  Verifies the software’s capabilities to perform transformations for Longitude Rotation method.
 *
 * <table class="gigs">
 * <caption>Test description</caption>
 * <tr>
 *   <th>Test method:</th>
 *   <td><ul>
 *      <li>Invoke GIGS transformation “GIGS geogCRS H to GIGS geogCRS T (1)”, GIGS code 61763 in both
 *      directions and inspect results</li>
 *      <li>For the first test point perform iterations of forward and reverse computations using the output
 *      of computation n as input into computation n+1, until the output coordinate values exceed more
 *      than 0.00000006° from the original calculated values (but no more than 1000 iterations).</li></ul>
 *   </td>
 * </tr><tr>
 *   <th>Test data folder:</th>
 *   <td><a href="https://github.com/IOGP-GIGS/GIGSTestDataset/blob/main/GIGSTestDatasetFiles/GIGS%205200%20Coordinate%20transformation%20test%20data/ASCII/GIGS_tfm_5208_LonRot_input.txt">{@code GIGS_tfm_5208_LonRot_input.txt}</a>
 * </tr><tr>
 *   <th>Tested API:</th>
 *   <td>{@link CoordinateOperationFactory#createOperation(CoordinateReferenceSystem, CoordinateReferenceSystem)}.</td>
 * </tr><tr>
 *   <th>Expected result:</th>
 *   <td>Results should agree to within 0.03m (30mm) or 0.0000003 grad (dependent on latitude) of the
 *      Test Data. See file GIGS_tfm_5208_LonRot_output.
 *   <p>For the round trip calculation the initial calculated coordinates of the point should change by less
 *      than 0.006m (6mm) or 0.00000006 grad (before 1000 iterations).
 *   <p>Test result will be pass or fail. If fail, details of failure should be reported.</td>
 * </tr></table>
 *
 *
 * <h2>Usage example</h2>
 * In order to specify their factories and run the tests in a JUnit framework, implementers can
 * define a subclass in their own test suite as in the example below:
 *
 * {@snippet lang = "java":
 * public class MyTest extends Test5208 {
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
@DisplayName("Longitude Rotation")
public class Test5208 extends Series5000 {
    /**
     * Data about the CRS of the geographic CRS.
     *
     * @see #createGeogCRSH(TestMethod)
     */
    final Test3205 geogCRSHTest;

    /**
     * Data about the CRS of the geographic CRS.
     *
     * @see #createGeogCRST(TestMethod)
     */
    final Test3205 geogCRSTTest;

    /**
     * The geographic CRS created by this factory.
     */
    protected GeographicCRS geogCRSH;

    /**
     * The geographic CRS created by the factory,
     * or {@code null} if not yet created or if geographic CRS creation failed.
     */
    protected GeographicCRS geogCRST;


    /**
     * Geographic tolerance in radians
     */
    double geoRadTolerance = 0.0000003;
    /**
     * Round Trip Geographic Tolerance in radians
     */
    double rtGeoRadTolerance = 0.00000006;

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
    public Test5208(final Factories factories) throws FactoryException {
        super( 0.03, 0.0000003 * Math.PI / 180, 0.006, 0.00000006 * Math.PI / 180);
        copFactory = factories.copFactory;

        geogCRSHTest = new Test3205(factories);
        geogCRSHTest.skipTests = true;
        geogCRSHTest.skipIdentificationCheck = true;

        geogCRSTTest = new Test3205(factories);
        geogCRSTTest.skipTests = true;
        geogCRSTTest.skipIdentificationCheck = true;

        createCRSs();
        createCoOp();
    }

    /**
     * Method used in class instantiation to initialise the geographic CRS E and the geographic CRS A
     * used in this test class.
     * <p>Geographic CRS T: GIGS CRS Code 64013; GIGS geogCRS T; NTF; decimal degree; EPSG CRS code 4275
     * <p>Geographic CRS H: GIGS CRS Code 64011; GIGS geogCRS H; NTF (Paris); gradians; EPSG CRS code 4807
     *
     * @throws FactoryException if an error occurred while creating a CRS.
     */
    @Override
    protected void createCRSs() throws FactoryException {
        createGeogCRST(Test3205::GIGS_64013);
        createGeogCRSH(Test3205::GIGS_64011);
    }

    @Override
    protected void createCoOp() throws FactoryException {
        forwardCoOp = copFactory.createOperation(geogCRST, geogCRSH);
        reverseCoOp = copFactory.createOperation(geogCRSH, geogCRST);
    }

    /**
     * Creates a user-defined geographic CRS by executing the specified method from the {@link Test3205} class.
     *
     * @param  factory          the test method to use for creating the geographic CRS.
     * @throws FactoryException if an error occurred while creating the geographic CRS.
     */
    void createGeogCRSH(final TestMethod<Test3205> factory) throws FactoryException {
        factory.initialize(geogCRSHTest);
        geogCRSH = (GeographicCRS) geogCRSHTest.getIdentifiedObject();
    }

    /**
     * Creates a user-defined geographic CRS by executing the specified method from the {@link Test3205} class.
     *
     * @param  factory          the test method to use for creating the geographic CRS.
     * @throws FactoryException if an error occurred while creating the geographic CRS.
     */
    void createGeogCRST(final TestMethod<Test3205> factory) throws FactoryException {
        factory.initialize(geogCRSTTest);
        geogCRST = (GeographicCRS) geogCRSTTest.getIdentifiedObject();
    }

    /**
     * Verifies the result of the conversion produced by {@link #convertPoint(double[])} ()}.
     *
     * @param expectedPoint the expected coordinates from the GIGS output file.
     * @param resPoint the coordinates computed by the factory.
     */
    @Override
    void verifyConversion(double[] expectedPoint, double[] resPoint) {
        if (isForward) {
            assertArrayEquals(expectedPoint, resPoint, isRTC ? rtGeoRadTolerance : geoRadTolerance);
        } else {
            assertArrayEquals(expectedPoint, resPoint, isRTC ? rtGeoTolerance : geoTolerance);
        }
    }

    /**
     * Tests “GIGS-5208-01” for LonRot transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5208-01</b></li>
     *   <li>Latitude (GIGS CRS Code 64013; GIGS geogCRS T; NTF; decimal degree; EPSG CRS code 4275): <b>58</b></li>
     *   <li>Longitude (GIGS CRS Code 64013; GIGS geogCRS T; NTF; decimal degree; EPSG CRS code 4275): <b>5</b></li>
     *   <li>Latitude (GIGS CRS Code 64011; GIGS geogCRS H; NTF (Paris); gradians; EPSG CRS code 4807): <b>64.44444444</b></li>
     *   <li>Longitude (GIGS CRS Code 64011; GIGS geogCRS H; NTF (Paris); gradians; EPSG CRS code 4807): <b>2.958634256</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: Round Trip calculation point.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5208-01 : REVERSE")
    public void GIGS_5208_01() throws TransformException {
        isRTC = true;
        isForward = false;
        final double[] originPoint = new double[]{64.44444444, 2.958634256};
        final double[] destinationPoint = new double[]{58, 5};
        convertAndVerifyRoundTripPoint(originPoint, destinationPoint);
    }

    /**
     * Tests “GIGS-5208-02” for LonRot transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5208-02</b></li>
     *   <li>Latitude (GIGS CRS Code 64013; GIGS geogCRS T; NTF; decimal degree; EPSG CRS code 4275): <b>56</b></li>
     *   <li>Longitude (GIGS CRS Code 64013; GIGS geogCRS T; NTF; decimal degree; EPSG CRS code 4275): <b>5</b></li>
     *   <li>Latitude (GIGS CRS Code 64011; GIGS geogCRS H; NTF (Paris); gradians; EPSG CRS code 4807): <b>62.22222222</b></li>
     *   <li>Longitude (GIGS CRS Code 64011; GIGS geogCRS H; NTF (Paris); gradians; EPSG CRS code 4807): <b>2.958634256</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5208-02 : FORWARD")
    public void GIGS_5208_02() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{56, 5};
        final double[] destinationPoint = new double[]{62.22222222, 2.958634256};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5208-03” for LonRot transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5208-03</b></li>
     *   <li>Latitude (GIGS CRS Code 64013; GIGS geogCRS T; NTF; decimal degree; EPSG CRS code 4275): <b>55</b></li>
     *   <li>Longitude (GIGS CRS Code 64013; GIGS geogCRS T; NTF; decimal degree; EPSG CRS code 4275): <b>5</b></li>
     *   <li>Latitude (GIGS CRS Code 64011; GIGS geogCRS H; NTF (Paris); gradians; EPSG CRS code 4807): <b>61.11111111</b></li>
     *   <li>Longitude (GIGS CRS Code 64011; GIGS geogCRS H; NTF (Paris); gradians; EPSG CRS code 4807): <b>2.958634256</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5208-03 : REVERSE")
    public void GIGS_5208_03() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{61.11111111, 2.958634256};
        final double[] destinationPoint = new double[]{55, 5};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5208-04” for LonRot transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5208-04</b></li>
     *   <li>Latitude (GIGS CRS Code 64013; GIGS geogCRS T; NTF; decimal degree; EPSG CRS code 4275): <b>53</b></li>
     *   <li>Longitude (GIGS CRS Code 64013; GIGS geogCRS T; NTF; decimal degree; EPSG CRS code 4275): <b>5</b></li>
     *   <li>Latitude (GIGS CRS Code 64011; GIGS geogCRS H; NTF (Paris); gradians; EPSG CRS code 4807): <b>58.88888889</b></li>
     *   <li>Longitude (GIGS CRS Code 64011; GIGS geogCRS H; NTF (Paris); gradians; EPSG CRS code 4807): <b>2.958634256</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5208-04 : FORWARD")
    public void GIGS_5208_04() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{53, 5};
        final double[] destinationPoint = new double[]{58.88888889, 2.958634256};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5208-05” for LonRot transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5208-05</b></li>
     *   <li>Latitude (GIGS CRS Code 64013; GIGS geogCRS T; NTF; decimal degree; EPSG CRS code 4275): <b>51</b></li>
     *   <li>Longitude (GIGS CRS Code 64013; GIGS geogCRS T; NTF; decimal degree; EPSG CRS code 4275): <b>4</b></li>
     *   <li>Latitude (GIGS CRS Code 64011; GIGS geogCRS H; NTF (Paris); gradians; EPSG CRS code 4807): <b>56.66666667</b></li>
     *   <li>Longitude (GIGS CRS Code 64011; GIGS geogCRS H; NTF (Paris); gradians; EPSG CRS code 4807): <b>1.847523144</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5208-05 : REVERSE")
    public void GIGS_5208_05() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{56.66666667, 1.847523144};
        final double[] destinationPoint = new double[]{51, 4};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5208-06” for LonRot transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5208-06</b></li>
     *   <li>Latitude (GIGS CRS Code 64013; GIGS geogCRS T; NTF; decimal degree; EPSG CRS code 4275): <b>49</b></li>
     *   <li>Longitude (GIGS CRS Code 64013; GIGS geogCRS T; NTF; decimal degree; EPSG CRS code 4275): <b>4</b></li>
     *   <li>Latitude (GIGS CRS Code 64011; GIGS geogCRS H; NTF (Paris); gradians; EPSG CRS code 4807): <b>54.44444444</b></li>
     *   <li>Longitude (GIGS CRS Code 64011; GIGS geogCRS H; NTF (Paris); gradians; EPSG CRS code 4807): <b>1.847523144</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5208-06 : FORWARD")
    public void GIGS_5208_06() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{49, 4};
        final double[] destinationPoint = new double[]{54.44444444, 1.847523144};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5208-07” for LonRot transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5208-07</b></li>
     *   <li>Latitude (GIGS CRS Code 64013; GIGS geogCRS T; NTF; decimal degree; EPSG CRS code 4275): <b>46.8</b></li>
     *   <li>Longitude (GIGS CRS Code 64013; GIGS geogCRS T; NTF; decimal degree; EPSG CRS code 4275): <b>2.33722917</b></li>
     *   <li>Latitude (GIGS CRS Code 64011; GIGS geogCRS H; NTF (Paris); gradians; EPSG CRS code 4807): <b>52</b></li>
     *   <li>Longitude (GIGS CRS Code 64011; GIGS geogCRS H; NTF (Paris); gradians; EPSG CRS code 4807): <b>0</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5208-07 : REVERSE")
    public void GIGS_5208_07() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{52, 0};
        final double[] destinationPoint = new double[]{46.8, 2.33722917};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5208-08” for LonRot transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5208-08</b></li>
     *   <li>Latitude (GIGS CRS Code 64013; GIGS geogCRS T; NTF; decimal degree; EPSG CRS code 4275): <b>53</b></li>
     *   <li>Longitude (GIGS CRS Code 64013; GIGS geogCRS T; NTF; decimal degree; EPSG CRS code 4275): <b>3</b></li>
     *   <li>Latitude (GIGS CRS Code 64011; GIGS geogCRS H; NTF (Paris); gradians; EPSG CRS code 4807): <b>58.88888889</b></li>
     *   <li>Longitude (GIGS CRS Code 64011; GIGS geogCRS H; NTF (Paris); gradians; EPSG CRS code 4807): <b>0.736412033</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5208-08 : REVERSE")
    public void GIGS_5208_08() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{58.88888889, 0.736412033};
        final double[] destinationPoint = new double[]{53, 3};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5208-09” for LonRot transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5208-09</b></li>
     *   <li>Latitude (GIGS CRS Code 64013; GIGS geogCRS T; NTF; decimal degree; EPSG CRS code 4275): <b>53</b></li>
     *   <li>Longitude (GIGS CRS Code 64013; GIGS geogCRS T; NTF; decimal degree; EPSG CRS code 4275): <b>4</b></li>
     *   <li>Latitude (GIGS CRS Code 64011; GIGS geogCRS H; NTF (Paris); gradians; EPSG CRS code 4807): <b>58.88888889</b></li>
     *   <li>Longitude (GIGS CRS Code 64011; GIGS geogCRS H; NTF (Paris); gradians; EPSG CRS code 4807): <b>1.847523144</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5208-09 : FORWARD")
    public void GIGS_5208_09() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{53, 4};
        final double[] destinationPoint = new double[]{58.88888889, 1.847523144};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5208-10” for LonRot transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5208-10</b></li>
     *   <li>Latitude (GIGS CRS Code 64013; GIGS geogCRS T; NTF; decimal degree; EPSG CRS code 4275): <b>53</b></li>
     *   <li>Longitude (GIGS CRS Code 64013; GIGS geogCRS T; NTF; decimal degree; EPSG CRS code 4275): <b>6</b></li>
     *   <li>Latitude (GIGS CRS Code 64011; GIGS geogCRS H; NTF (Paris); gradians; EPSG CRS code 4807): <b>58.88888889</b></li>
     *   <li>Longitude (GIGS CRS Code 64011; GIGS geogCRS H; NTF (Paris); gradians; EPSG CRS code 4807): <b>4.069745367</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5208-10 : REVERSE")
    public void GIGS_5208_10() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{58.88888889, 4.069745367};
        final double[] destinationPoint = new double[]{53, 6};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5208-11” for LonRot transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5208-11</b></li>
     *   <li>Latitude (GIGS CRS Code 64013; GIGS geogCRS T; NTF; decimal degree; EPSG CRS code 4275): <b>53</b></li>
     *   <li>Longitude (GIGS CRS Code 64013; GIGS geogCRS T; NTF; decimal degree; EPSG CRS code 4275): <b>7</b></li>
     *   <li>Latitude (GIGS CRS Code 64011; GIGS geogCRS H; NTF (Paris); gradians; EPSG CRS code 4807): <b>58.88888889</b></li>
     *   <li>Longitude (GIGS CRS Code 64011; GIGS geogCRS H; NTF (Paris); gradians; EPSG CRS code 4807): <b>5.180856478</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5208-11 : FORWARD")
    public void GIGS_5208_11() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{53, 7};
        final double[] destinationPoint = new double[]{58.88888889, 5.180856478};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5208-12” for LonRot transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5208-12</b></li>
     *   <li>Latitude (GIGS CRS Code 64013; GIGS geogCRS T; NTF; decimal degree; EPSG CRS code 4275): <b>53</b></li>
     *   <li>Longitude (GIGS CRS Code 64013; GIGS geogCRS T; NTF; decimal degree; EPSG CRS code 4275): <b>9</b></li>
     *   <li>Latitude (GIGS CRS Code 64011; GIGS geogCRS H; NTF (Paris); gradians; EPSG CRS code 4807): <b>58.88888889</b></li>
     *   <li>Longitude (GIGS CRS Code 64011; GIGS geogCRS H; NTF (Paris); gradians; EPSG CRS code 4807): <b>7.4030787</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5208-12 : REVERSE")
    public void GIGS_5208_12() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{58.88888889, 7.4030787};
        final double[] destinationPoint = new double[]{53, 9};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5208-13” for LonRot transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5208-13</b></li>
     *   <li>Latitude (GIGS CRS Code 64013; GIGS geogCRS T; NTF; decimal degree; EPSG CRS code 4275): <b>53</b></li>
     *   <li>Longitude (GIGS CRS Code 64013; GIGS geogCRS T; NTF; decimal degree; EPSG CRS code 4275): <b>10</b></li>
     *   <li>Latitude (GIGS CRS Code 64011; GIGS geogCRS H; NTF (Paris); gradians; EPSG CRS code 4807): <b>58.88888889</b></li>
     *   <li>Longitude (GIGS CRS Code 64011; GIGS geogCRS H; NTF (Paris); gradians; EPSG CRS code 4807): <b>8.514189811</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5208-13 : FORWARD")
    public void GIGS_5208_13() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{53, 10};
        final double[] destinationPoint = new double[]{58.88888889, 8.514189811};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5208-14” for LonRot transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5208-14</b></li>
     *   <li>Latitude (GIGS CRS Code 64013; GIGS geogCRS T; NTF; decimal degree; EPSG CRS code 4275): <b>53</b></li>
     *   <li>Longitude (GIGS CRS Code 64013; GIGS geogCRS T; NTF; decimal degree; EPSG CRS code 4275): <b>11</b></li>
     *   <li>Latitude (GIGS CRS Code 64011; GIGS geogCRS H; NTF (Paris); gradians; EPSG CRS code 4807): <b>58.88888889</b></li>
     *   <li>Longitude (GIGS CRS Code 64011; GIGS geogCRS H; NTF (Paris); gradians; EPSG CRS code 4807): <b>9.625300922</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5208-14 : REVERSE")
    public void GIGS_5208_14() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{58.88888889, 9.625300922};
        final double[] destinationPoint = new double[]{53, 11};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }
}
