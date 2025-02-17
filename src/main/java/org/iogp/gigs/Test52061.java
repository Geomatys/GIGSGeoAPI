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

import org.junit.jupiter.api.Assertions;
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
 *  Verifies the software’s capabilities to perform transformations for NADCON  method.
 *
 * <table class="gigs">
 * <caption>Test description</caption>
 * <tr>
 *   <th>Test method:</th>
 *   <td><ul>
 *      <li>Invoke respective grid-based transformations in both directions and inspect results, for either
* part 1 or part 2 and part 3.</li>
 *      <li>This test uses two pairs of binary gridded data files [Alaska.las Alaska.los; Conus.las Conus.
 *      los]. It is assumed that these files are included in the application being tested. If necessary, the
 *      gridded data files may be downloaded from NGS. The equivalent EPSG transformation names are: NAD27 to
 *      NAD83 (2) (code 1243) and NAD27 to NAD83 (1) (code 1241).</li>
 *      <li>For the first test point perform iterations of forward and reverse computations using the output
 *      of computation n as input into computation n+1, until the output coordinate values exceed more
 *      than 0.00000006° from the original calculated values (but no more than 1000 iterations).</li></ul>
 *   </td>
 * </tr><tr>
 *   <th>Test data folder:</th>
 *   <td><a href="https://github.com/IOGP-GIGS/GIGSTestDataset/blob/main/GIGSTestDatasetFiles/GIGS%205200%20Coordinate%20transformation%20test%20data/ASCII/GIGS_tfm_5206_Nadcon_input_part1.txt">{@code GIGS_tfm_5206_Nadcon_input_part1.txt}</a>
 * </tr><tr>
 *   <th>Tested API:</th>
 *   <td>{@link CoordinateOperationFactory#createOperation(CoordinateReferenceSystem, CoordinateReferenceSystem)}.</td>
 * </tr><tr>
 *   <th>Expected result:</th>
 *   <td>Results should agree to within 0.03m (30mm) or 0.0000003° of the Test Data.
 *   <p>See file GIGS_tfm_5206_Nadcon_output_part[x].
 *   <p>For the round trip calculation the initial calculated coordinates of the point should change by less
 *      than 0.006m (6mm) or 0.00000006° (before 1000 iterations).
 *   <p>Test result will be pass or fail. If fail, details of failure should be reported.</td>
 * </tr></table>
 *
 *
 * <h2>Usage example</h2>
 * In order to specify their factories and run the tests in a JUnit framework, implementers can
 * define a subclass in their own test suite as in the example below:
 *
 * {@snippet lang = "java":
 * public class MyTest extends Test52061 {
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
@DisplayName("NADCON")
public class Test52061 extends Series5000 {
    /**
     * Data about the CRS of the geographic CRS.
     *
     * @see #createGeogCRSJ(TestMethod)
     */
    final Test3205 geogCRSJTest;

    /**
     * Data about the CRS of the geographic CRS.
     *
     * @see #createGeogCRSZ(TestMethod)
     */
    final Test3205 geogCRSZTest;

    /**
     * The geographic CRS created by this factory.
     */
    protected GeographicCRS geogCRSJ;

    /**
     * The geographic CRS created by the factory,
     * or {@code null} if not yet created or if geographic CRS creation failed.
     */
    protected GeographicCRS geogCRSZ;

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
    public Test52061(final Factories factories) throws FactoryException {
        super( 0.03, 0.0000003, 0.006, 0.00000006);
        copFactory = factories.copFactory;

        geogCRSJTest = new Test3205(factories);
        geogCRSJTest.skipTests = true;
        geogCRSJTest.skipIdentificationCheck = true;

        geogCRSZTest = new Test3205(factories);
        geogCRSZTest.skipTests = true;
        geogCRSZTest.skipIdentificationCheck = true;

        createCRSs();
        createCoOp();
    }

    /**
     * Method used in class instantiation to initialise the geographic CRS E and the geographic CRS A
     * used in this test class.
     * <p>Geographic CRS J: GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267
     * <p>Geographic CRS Z: GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269
     *
     * @throws FactoryException if an error occurred while creating a CRS.
     */
    @Override
    protected void createCRSs() throws FactoryException {
        createGeogCRSJ(Test3205::GIGS_64012);
        createGeogCRSZ(Test3205::GIGS_64018);
    }

    @Override
    protected void createCoOp() throws FactoryException {
        forwardCoOp = copFactory.createOperation(geogCRSJ, geogCRSZ);
        reverseCoOp = copFactory.createOperation(geogCRSZ, geogCRSJ);
    }

    /**
     * Creates a user-defined geographic CRS by executing the specified method from the {@link Test3205} class.
     *
     * @param  factory          the test method to use for creating the geographic CRS.
     * @throws FactoryException if an error occurred while creating the geographic CRS.
     */
    void createGeogCRSJ(final TestMethod<Test3205> factory) throws FactoryException {
        factory.initialize(geogCRSJTest);
        geogCRSJ = (GeographicCRS) geogCRSJTest.getIdentifiedObject();
    }

    /**
     * Creates a user-defined geographic CRS by executing the specified method from the {@link Test3205} class.
     *
     * @param  factory          the test method to use for creating the geographic CRS.
     * @throws FactoryException if an error occurred while creating the geographic CRS.
     */
    void createGeogCRSZ(final TestMethod<Test3205> factory) throws FactoryException {
        factory.initialize(geogCRSZTest);
        geogCRSZ = (GeographicCRS) geogCRSZTest.getIdentifiedObject();
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
     * Tests “GIGS-5206-01” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-01</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-179</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>69.9990142</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-179.0035064</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: Round Trip calculation point.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-01 : FORWARD")
    public void GIGS_5206_01() throws TransformException {
        isRTC = true;
        isForward = true;
        final double[] originPoint = new double[]{70, -179};
        final double[] destinationPoint = new double[]{69.9990142, -179.0035064};
        convertAndVerifyRoundTripPoint(originPoint, destinationPoint);
    }

    /**
     * Tests “GIGS-5206-02” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-02</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>70.0010089</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-179.9964664</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>180</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-02 : REVERSE")
    public void GIGS_5206_02() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{70, 180};
        final double[] destinationPoint = new double[]{70.0010089, -179.9964664};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-03” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-03</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>180</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>69.9989911</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>179.9964664</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-03 : FORWARD")
    public void GIGS_5206_03() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{70, 180};
        final double[] destinationPoint = new double[]{69.9989911, 179.9964664};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-04” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-04</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>179</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>69.9989678</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>178.9964394</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-04 : FORWARD")
    public void GIGS_5206_04() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{70, 179};
        final double[] destinationPoint = new double[]{69.9989678, 178.9964394};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-05” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-05</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>165</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>NaN</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>NaN</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     *   <li>Value outside tfm grid; should fail</li>
     * </ul>
     */
    @Test
    @DisplayName("GIGS-5206-05 : FORWARD")
    public void GIGS_5206_05() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{70, 165};
        Assertions.assertThrowsExactly(TransformException.class, () -> convertPoint(originPoint));
    }

    /**
     * Tests “GIGS-5206-06” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-06</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>70.0001631</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-142.9972522</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-143</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NTv2 test 5207.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-06 : REVERSE")
    public void GIGS_5206_06() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{70, -143};
        final double[] destinationPoint = new double[]{70.0001631, -142.9972522};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-07” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-07</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-142</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>69.9998422</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-142.0026922</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: NAD27 position also used in NTv2 test 5207.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-07 : FORWARD")
    public void GIGS_5206_07() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{70, -142};
        final double[] destinationPoint = new double[]{69.9998422, -142.0026922};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-08” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-08</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>70.0001578</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-141.9973078</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-142</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NTv2 test 5207.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-08 : REVERSE")
    public void GIGS_5206_08() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{70, -142};
        final double[] destinationPoint = new double[]{70.0001578, -141.9973078};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-09” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-09</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>70.0001481</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-140.9971981</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-141</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NTv2 test 5207.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-09 : REVERSE")
    public void GIGS_5206_09() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{70, -141};
        final double[] destinationPoint = new double[]{70.0001481, -140.9971981};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-10” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-10</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-140</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>69.9998617</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-140.0027922</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: NAD27 position also used in NTv2 test 5207.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-10 : FORWARD")
    public void GIGS_5206_10() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{70, -140};
        final double[] destinationPoint = new double[]{69.9998617, -140.0027922};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-11” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-11</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>70.0001297</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-138.9973075</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-139</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NTv2 test 5207.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-11 : REVERSE")
    public void GIGS_5206_11() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{70, -139};
        final double[] destinationPoint = new double[]{70.0001297, -138.9973075};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-12” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-12</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-138</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>69.9998867</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-138.0026767</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: NAD27 position also used in NTv2 test 5207.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-12 : FORWARD")
    public void GIGS_5206_12() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{70, -138};
        final double[] destinationPoint = new double[]{69.9998867, -138.0026767};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-13” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-13</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>70.000055</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-134.9973733</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-135</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NTv2 test 5207.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-13 : REVERSE")
    public void GIGS_5206_13() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{70, -135};
        final double[] destinationPoint = new double[]{70.000055, -134.9973733};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-14” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-14</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-130</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>70.0000511</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-130.0025347</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: NAD27 position also used in NTv2 test 5207.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-14 : FORWARD")
    public void GIGS_5206_14() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{70, -130};
        final double[] destinationPoint = new double[]{70.0000511, -130.0025347};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-15” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-15</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>69.9999053</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-128.0009742</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-128.0034722</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NTv2 test 5207.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-15 : REVERSE")
    public void GIGS_5206_15() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{70, -128.0034722};
        final double[] destinationPoint = new double[]{69.9999053, -128.0009742};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-16” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-16</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-128</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>NaN</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>NaN</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     *   <li>Value outside tfm grid; should fail</li>
     * </ul>
     *
     * Remarks: NAD27 position also used in NTv2 test 5207.
     */
    @Test
    @DisplayName("GIGS-5206-16 : FORWARD")
    public void GIGS_5206_16() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{70, -128};
        Assertions.assertThrowsExactly(TransformException.class, () -> convertPoint(originPoint));
    }

    /**
     * Tests “GIGS-5206-17” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-17</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>NaN</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>NaN</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-126</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     *   <li>Value outside tfm grid; should fail</li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NTv2 test 5207.
     */
    @Test
    @DisplayName("GIGS-5206-17 : REVERSE")
    public void GIGS_5206_17() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{70, -126};
        Assertions.assertThrowsExactly(TransformException.class, () -> convertPoint(originPoint));
    }

    /**
     * Tests “GIGS-5206-18” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-18</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>48.1169867</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-132.9984775</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>48.1166667</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-133</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NTv2 test 5207.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-18 : REVERSE")
    public void GIGS_5206_18() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{48.1166667, -133};
        final double[] destinationPoint = new double[]{48.1169867, -132.9984775};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-19” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-19</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>48.1166667</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-131.4555556</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>48.1163964</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-131.4571578</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: NAD27 position also used in NTv2 test 5207.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-19 : FORWARD")
    public void GIGS_5206_19() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{48.1166667, -131.4555556};
        final double[] destinationPoint = new double[]{48.1163964, -131.4571578};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-20” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-20</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>48.1169228</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-130.5161675</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>48.1166667</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-130.5177467</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NTv2 test 5207.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-20 : REVERSE")
    public void GIGS_5206_20() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{48.1166667, -130.5177467};
        final double[] destinationPoint = new double[]{48.1169228, -130.5161675};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-21” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-21</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>48.1166667</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-128.4555556</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>48.1164319</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-128.4570433</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: NAD27 position also used in NTv2 test 5207.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-21 : FORWARD")
    public void GIGS_5206_21() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{48.1166667, -128.4555556};
        final double[] destinationPoint = new double[]{48.1164319, -128.4570433};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-22” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-22</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>48.1166667</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-128.5444444</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>48.1164311</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-128.5459361</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NTv2 test 5207.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-22 : REVERSE")
    public void GIGS_5206_22() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{48.1164311, -128.5459361};
        final double[] destinationPoint = new double[]{48.1166667, -128.5444444};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-23” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-23</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>51</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-130.5177467</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>50.9997114</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-130.5193953</b></li>
     *   <li>Transect: <b>D</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: NAD27 position also used in NTv2 test 5207.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-23 : FORWARD")
    public void GIGS_5206_23() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{51, -130.5177467};
        final double[] destinationPoint = new double[]{50.9997114, -130.5193953};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-24” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-24</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>51.0002886</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-130.5160981</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>51</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-130.5177467</b></li>
     *   <li>Transect: <b>D</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NTv2 test 5207.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-24 : REVERSE")
    public void GIGS_5206_24() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{51, -130.5177467};
        final double[] destinationPoint = new double[]{51.0002886, -130.5160981};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-25” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-25</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>50.0002897</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-130.5161206</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>49.9999997</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-130.5177467</b></li>
     *   <li>Transect: <b>D</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NTv2 test 5207.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-25 : REVERSE")
    public void GIGS_5206_25() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{49.9999997, -130.5177467};
        final double[] destinationPoint = new double[]{50.0002897, -130.5161206};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-26” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-26</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>49.9999997</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-130.5177467</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>49.9997097</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-130.5193731</b></li>
     *   <li>Transect: <b>D</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: NAD27 position also used in NTv2 test 5207.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-26 : FORWARD")
    public void GIGS_5206_26() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{49.9999997, -130.5177467};
        final double[] destinationPoint = new double[]{49.9997097, -130.5193731};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-27” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-27</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>49.0002717</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-130.5161458</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>49</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-130.5177467</b></li>
     *   <li>Transect: <b>D</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NTv2 test 5207.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-27 : REVERSE")
    public void GIGS_5206_27() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{49, -130.5177467};
        final double[] destinationPoint = new double[]{49.0002717, -130.5161458};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-28” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-28</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>48.1166667</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-130.5177467</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>48.1164106</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-130.5193258</b></li>
     *   <li>Transect: <b>D</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-28 : FORWARD")
    public void GIGS_5206_28() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{48.1166667, -130.5177467};
        final double[] destinationPoint = new double[]{48.1164106, -130.5193258};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-29” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-29</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>47.0002378</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-130.5161942</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>47</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-130.5177467</b></li>
     *   <li>Transect: <b>D</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NTv2 test 5207.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-29 : REVERSE")
    public void GIGS_5206_29() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{47, -130.5177467};
        final double[] destinationPoint = new double[]{47.0002378, -130.5161942};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-30” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-30</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>47</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-130.5177467</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>46.9997622</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-130.5192992</b></li>
     *   <li>Transect: <b>D</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: NAD27 position also used in NTv2 test 5207.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-30 : FORWARD")
    public void GIGS_5206_30() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{47, -130.5177467};
        final double[] destinationPoint = new double[]{46.9997622, -130.5192992};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-31” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-31</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>46.0002222</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-130.5162172</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>46</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-130.5177467</b></li>
     *   <li>Transect: <b>D</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NTv2 test 5207.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-31 : REVERSE")
    public void GIGS_5206_31() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{46, -130.5177467};
        final double[] destinationPoint = new double[]{46.0002222, -130.5162172};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-32” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-32</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>45</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-130.5177467</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>44.9997925</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-130.5192539</b></li>
     *   <li>Transect: <b>D</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: NAD27 position also used in NTv2 test 5207.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-32 : FORWARD")
    public void GIGS_5206_32() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{45, -130.5177467};
        final double[] destinationPoint = new double[]{44.9997925, -130.5192539};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-33” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-33</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>51</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-112</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>NaN</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>NaN</b></li>
     *   <li>Transect: <b>E</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     *   <li>Value outside tfm grid; should fail</li>
     * </ul>
     */
    @Test
    @DisplayName("GIGS-5206-33 : FORWARD")
    public void GIGS_5206_33() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{51, -112};
        Assertions.assertThrowsExactly(TransformException.class, () -> convertPoint(originPoint));
    }

    /**
     * Tests “GIGS-5206-34” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-34</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>NaN</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>NaN</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>51</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-112</b></li>
     *   <li>Transect: <b>E</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     *   <li>Value outside tfm grid; should fail</li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NTv2 test 5207.
     */
    @Test
    @DisplayName("GIGS-5206-34 : REVERSE")
    public void GIGS_5206_34() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{51, -112};
        Assertions.assertThrowsExactly(TransformException.class, () -> convertPoint(originPoint));
    }

    /**
     * Tests “GIGS-5206-35” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-35</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>49.9999556</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-111.9991714</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>49.9999997</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-112</b></li>
     *   <li>Transect: <b>E</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NTv2 test 5207.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-35 : REVERSE")
    public void GIGS_5206_35() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{49.9999997, -112};
        final double[] destinationPoint = new double[]{49.9999556, -111.9991714};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-36” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-36</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>49.9999997</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-112</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>50.0000439</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-112.0008286</b></li>
     *   <li>Transect: <b>E</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: NAD27 position also used in NTv2 test 5207.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-36 : FORWARD")
    public void GIGS_5206_36() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{49.9999997, -112};
        final double[] destinationPoint = new double[]{50.0000439, -112.0008286};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-37” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-37</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>49.0000033</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-111.9991214</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>49</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-112</b></li>
     *   <li>Transect: <b>E</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NTv2 test 5207.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-37 : REVERSE")
    public void GIGS_5206_37() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{49, -112};
        final double[] destinationPoint = new double[]{49.0000033, -111.9991214};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-38” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-38</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>48</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-112</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>47.9999606</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-112.0008492</b></li>
     *   <li>Transect: <b>E</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: NAD27 position also used in NTv2 test 5207.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-38 : FORWARD")
    public void GIGS_5206_38() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{48, -112};
        final double[] destinationPoint = new double[]{47.9999606, -112.0008492};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-39” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-39</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>47.0000569</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-111.9991678</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>47</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-112</b></li>
     *   <li>Transect: <b>E</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NTv2 test 5207.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-39 : REVERSE")
    public void GIGS_5206_39() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{47, -112};
        final double[] destinationPoint = new double[]{47.0000569, -111.9991678};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-40” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-40</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>47</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-112</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>46.9999431</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-112.0008325</b></li>
     *   <li>Transect: <b>E</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: NAD27 position also used in NTv2 test 5207.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-40 : FORWARD")
    public void GIGS_5206_40() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{47, -112};
        final double[] destinationPoint = new double[]{46.9999431, -112.0008325};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-41” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-41</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>46.0000733</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-111.9991756</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>46</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-112</b></li>
     *   <li>Transect: <b>E</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NTv2 test 5207.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-41 : REVERSE")
    public void GIGS_5206_41() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{46, -112};
        final double[] destinationPoint = new double[]{46.0000733, -111.9991756};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-42” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-42</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>45</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-112</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>44.9999197</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-112.0008022</b></li>
     *   <li>Transect: <b>E</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-42 : FORWARD")
    public void GIGS_5206_42() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{45, -112};
        final double[] destinationPoint = new double[]{44.9999197, -112.0008022};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-43” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-43</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>29.9997978</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-89.5177272</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>30</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-89.5177778</b></li>
     *   <li>Transect: <b>F</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-43 : REVERSE")
    public void GIGS_5206_43() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{30, -89.5177778};
        final double[] destinationPoint = new double[]{29.9997978, -89.5177272};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-44” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-44</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>29.2833333</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-90.5177778</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>29.2835592</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-90.5178639</b></li>
     *   <li>Transect: <b>F</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-44 : FORWARD")
    public void GIGS_5206_44() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{29.2833333, -90.5177778};
        final double[] destinationPoint = new double[]{29.2835592, -90.5178639};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-45” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-45</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>28.2830864</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-91.5176758</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>28.2833333</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-91.5177778</b></li>
     *   <li>Transect: <b>F</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-45 : REVERSE")
    public void GIGS_5206_45() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{28.2833333, -91.5177778};
        final double[] destinationPoint = new double[]{28.2830864, -91.5176758};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-46” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-46</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>27.2833333</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-92.5177778</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>27.2836106</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-92.5178811</b></li>
     *   <li>Transect: <b>F</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-46 : FORWARD")
    public void GIGS_5206_46() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{27.2833333, -92.5177778};
        final double[] destinationPoint = new double[]{27.2836106, -92.5178811};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-47” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-47</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>26.2830225</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-93.5176731</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>26.2833333</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-93.5177778</b></li>
     *   <li>Transect: <b>F</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-47 : REVERSE")
    public void GIGS_5206_47() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{26.2833333, -93.5177778};
        final double[] destinationPoint = new double[]{26.2830225, -93.5176731};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-48” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-48</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>25</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-94.5177778</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>25.0003486</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-94.5178744</b></li>
     *   <li>Transect: <b>F</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5206-48 : FORWARD")
    public void GIGS_5206_48() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{25, -94.5177778};
        final double[] destinationPoint = new double[]{25.0003486, -94.5178744};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5206-49” for Nadcon transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5206-49</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>NaN</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>NaN</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>19.2833333</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-98.5177778</b></li>
     *   <li>Transect: <b>F</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     *   <li>Value outside tfm grid; should fail</li>
     * </ul>
     */
    @Test
    @DisplayName("GIGS-5206-49 : REVERSE")
    public void GIGS_5206_49() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{19.2833333, -98.5177778};
        Assertions.assertThrowsExactly(TransformException.class, () -> convertPoint(originPoint));
    }
}
