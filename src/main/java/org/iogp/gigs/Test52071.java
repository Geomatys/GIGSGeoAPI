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
 *  Verifies the software’s capabilities to perform transformations for NTv2 method.
 *
 * <table class="gigs">
 * <caption>Test description</caption>
 * <tr>
 *   <th>Test method:</th>
 *   <td><ul>
 *      <li>Invoke respective grid-based transformations in both directions and inspect results, for both parts.</li>
 *      <li>This test uses gridded data files [A66 National (13.09.01).gsb for Australia and NTv2.gsb for
 *      Canada]. It is assumed that these files are included in the application being tested. If necessary,
 *      the gridded data file for Australia may be downloaded from ICSM and for Canada from NRCAN.
 *      Equivalent EPSG transformation names and codes: AGD66 to GDA94 (11) (code 1803); NAD27 to
 *      NAD83 (4) (code 1313).</li>
 *      <li>For the first test point in part 1 and part 2 perform iterations of forward and reverse
 *      computations using the output of computation n as input into computation n+1, until the output
 *      coordinate values exceed more than 0.00000006° from the original calculated values (but no
 *      more than 1000 iterations).</li></ul>
 *   </td>
 * </tr><tr>
 *   <th>Test data folder:</th>
 *   <td><a href="https://github.com/IOGP-GIGS/GIGSTestDataset/blob/main/GIGSTestDatasetFiles/GIGS%205200%20Coordinate%20transformation%20test%20data/ASCII/GIGS_tfm_5207_NTv2_input_part1.txt">{@code GIGS_tfm_5207_NTv2_input_part1.txt}</a>
 * </tr><tr>
 *   <th>Tested API:</th>
 *   <td>{@link CoordinateOperationFactory#createOperation(CoordinateReferenceSystem, CoordinateReferenceSystem)}.</td>
 * </tr><tr>
 *   <th>Expected result:</th>
 *   <td>Results should agree to within 0.03m (30mm) or 0.0000003° of the Test Data.
 *   <p>See file GIGS_tfm_5207_NTv2_output_part[x].
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
 * public class MyTest extends Test52071 {
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
@DisplayName("NTv2")
public class Test52071 extends Series5000 {
    /**
     * Data about the CRS of the geographic CRS.
     *
     * @see #createGeogCRSX(TestMethod)
     */
    final Test3205 geogCRSXTest;

    /**
     * Data about the CRS of the geographic CRS.
     *
     * @see #createGeogCRSF(TestMethod)
     */
    final Test3205 geogCRSFTest;

    /**
     * The geographic CRS created by this factory.
     */
    protected GeographicCRS geogCRSX;

    /**
     * The geographic CRS created by the factory,
     * or {@code null} if not yet created or if geographic CRS creation failed.
     */
    protected GeographicCRS geogCRSF;

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
    public Test52071(final Factories factories) throws FactoryException {
        super( 0.03, 0.0000003, 0.006, 0.00000006);
        copFactory = factories.copFactory;

        geogCRSXTest = new Test3205(factories);
        geogCRSXTest.skipTests = true;
        geogCRSXTest.skipIdentificationCheck = true;

        geogCRSFTest = new Test3205(factories);
        geogCRSFTest.skipTests = true;
        geogCRSFTest.skipIdentificationCheck = true;

        createCRSs();
        createCoOp();
    }

    /**
     * Method used in class instantiation to initialise the geographic CRS E and the geographic CRS A
     * used in this test class.
     * <p>Geographic CRS X: GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202
     * <p>Geographic CRS F: GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283
     *
     * @throws FactoryException if an error occurred while creating a CRS.
     */
    @Override
    protected void createCRSs() throws FactoryException {
        createGeogCRSX(Test3205::GIGS_64016);
        createGeogCRSF(Test3205::GIGS_64009);
    }

    @Override
    protected void createCoOp() throws FactoryException {
        forwardCoOp = copFactory.createOperation(geogCRSX, geogCRSF);
        reverseCoOp = copFactory.createOperation(geogCRSF, geogCRSX);
    }

    /**
     * Creates a user-defined geographic CRS by executing the specified method from the {@link Test3205} class.
     *
     * @param  factory          the test method to use for creating the geographic CRS.
     * @throws FactoryException if an error occurred while creating the geographic CRS.
     */
    void createGeogCRSX(final TestMethod<Test3205> factory) throws FactoryException {
        factory.initialize(geogCRSXTest);
        geogCRSX = (GeographicCRS) geogCRSXTest.getIdentifiedObject();
    }

    /**
     * Creates a user-defined geographic CRS by executing the specified method from the {@link Test3205} class.
     *
     * @param  factory          the test method to use for creating the geographic CRS.
     * @throws FactoryException if an error occurred while creating the geographic CRS.
     */
    void createGeogCRSF(final TestMethod<Test3205> factory) throws FactoryException {
        factory.initialize(geogCRSFTest);
        geogCRSF = (GeographicCRS) geogCRSFTest.getIdentifiedObject();
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
     * Tests “GIGS-5207-01” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-01</b></li>
     *   <li>Latitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>-8</b></li>
     *   <li>Longitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>-115</b></li>
     *   <li>Latitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>NaN</b></li>
     *   <li>Longitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>NaN</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     *   <li>Value outside tfm grid; should fail</li>
     * </ul>
     *
     * Remarks: Round Trip calculation point.
     */
    @Test
    @DisplayName("GIGS-5207-01 : FORWARD")
    public void GIGS_5207_01() throws TransformException {
        isRTC = true;
        isForward = true;
        final double[] originPoint = new double[]{-8, -115};
        Assertions.assertThrowsExactly(TransformException.class, () -> convertPoint(originPoint));
    }

    /**
     * Tests “GIGS-5207-02” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-02</b></li>
     *   <li>Latitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>NaN</b></li>
     *   <li>Longitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>NaN</b></li>
     *   <li>Latitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>-9</b></li>
     *   <li>Longitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>-115</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     *   <li>Value outside tfm grid; should fail</li>
     * </ul>
     */
    @Test
    @DisplayName("GIGS-5207-02 : REVERSE")
    public void GIGS_5207_02() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-9, -115};
        Assertions.assertThrowsExactly(TransformException.class, () -> convertPoint(originPoint));
    }

    /**
     * Tests “GIGS-5207-03” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-03</b></li>
     *   <li>Latitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>-10</b></li>
     *   <li>Longitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>-115</b></li>
     *   <li>Latitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>NaN</b></li>
     *   <li>Longitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>NaN</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     *   <li>Value outside tfm grid; should fail</li>
     * </ul>
     */
    @Test
    @DisplayName("GIGS-5207-03 : FORWARD")
    public void GIGS_5207_03() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-10, -115};
        Assertions.assertThrowsExactly(TransformException.class, () -> convertPoint(originPoint));
    }

    /**
     * Tests “GIGS-5207-04” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-04</b></li>
     *   <li>Latitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>-10.05</b></li>
     *   <li>Longitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>115</b></li>
     *   <li>Latitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>-10.04863722</b></li>
     *   <li>Longitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>115.0012511</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-04 : FORWARD")
    public void GIGS_5207_04() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-10.05, 115};
        final double[] destinationPoint = new double[]{-10.04863722, 115.0012511};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-05” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-05</b></li>
     *   <li>Latitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>-11.00135944</b></li>
     *   <li>Longitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>114.9987442</b></li>
     *   <li>Latitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>-11</b></li>
     *   <li>Longitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>115</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-05 : REVERSE")
    public void GIGS_5207_05() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-11, 115};
        final double[] destinationPoint = new double[]{-11.00135944, 114.9987442};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-06” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-06</b></li>
     *   <li>Latitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>-12</b></li>
     *   <li>Longitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>115</b></li>
     *   <li>Latitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>-11.99864417</b></li>
     *   <li>Longitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>115.0012614</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-06 : FORWARD")
    public void GIGS_5207_06() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-12, 115};
        final double[] destinationPoint = new double[]{-11.99864417, 115.0012614};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-07” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-07</b></li>
     *   <li>Latitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>-9</b></li>
     *   <li>Longitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>138.04</b></li>
     *   <li>Latitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>-8.99854333</b></li>
     *   <li>Longitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>138.0411328</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-07 : FORWARD")
    public void GIGS_5207_07() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-9, 138.04};
        final double[] destinationPoint = new double[]{-8.99854333, 138.0411328};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-08” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-08</b></li>
     *   <li>Latitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>-9.00145667</b></li>
     *   <li>Longitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>138.0438672</b></li>
     *   <li>Latitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>-9</b></li>
     *   <li>Longitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>138.045</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-08 : REVERSE")
    public void GIGS_5207_08() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-9, 138.045};
        final double[] destinationPoint = new double[]{-9.00145667, 138.0438672};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-09” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-09</b></li>
     *   <li>Latitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>-9</b></li>
     *   <li>Longitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>138.05</b></li>
     *   <li>Latitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>-8.99854333</b></li>
     *   <li>Longitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>138.0511328</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-09 : REVERSE")
    public void GIGS_5207_09() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-8.99854333, 138.0511328};
        final double[] destinationPoint = new double[]{-9, 138.05};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-10” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-10</b></li>
     *   <li>Latitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>-9.00145667</b></li>
     *   <li>Longitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>138.0488672</b></li>
     *   <li>Latitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>-9</b></li>
     *   <li>Longitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>138.05</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-10 : REVERSE")
    public void GIGS_5207_10() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-9, 138.05};
        final double[] destinationPoint = new double[]{-9.00145667, 138.0488672};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-11” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-11</b></li>
     *   <li>Latitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>-9.00145667</b></li>
     *   <li>Longitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>138.0538672</b></li>
     *   <li>Latitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>-9</b></li>
     *   <li>Longitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>138.055</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-11 : REVERSE")
    public void GIGS_5207_11() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-9, 138.055};
        final double[] destinationPoint = new double[]{-9.00145667, 138.0538672};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-12” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-12</b></li>
     *   <li>Latitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>-9</b></li>
     *   <li>Longitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>138.06</b></li>
     *   <li>Latitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>-8.99854333</b></li>
     *   <li>Longitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>138.0611328</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-12 : FORWARD")
    public void GIGS_5207_12() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-9, 138.06};
        final double[] destinationPoint = new double[]{-8.99854333, 138.0611328};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-13” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-13</b></li>
     *   <li>Latitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>-27.12647639</b></li>
     *   <li>Longitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>138.0387308</b></li>
     *   <li>Latitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>-27.125</b></li>
     *   <li>Longitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>138.04</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-13 : FORWARD")
    public void GIGS_5207_13() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-27.12647639, 138.0387308};
        final double[] destinationPoint = new double[]{-27.125, 138.04};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-14” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-14</b></li>
     *   <li>Latitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>-27.125</b></li>
     *   <li>Longitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>138.045</b></li>
     *   <li>Latitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>-27.12352361</b></li>
     *   <li>Longitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>138.0462689</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-14 : REVERSE")
    public void GIGS_5207_14() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-27.12352361, 138.0462689};
        final double[] destinationPoint = new double[]{-27.125, 138.045};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-15” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-15</b></li>
     *   <li>Latitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>-27.125</b></li>
     *   <li>Longitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>138.05</b></li>
     *   <li>Latitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>-27.12352361</b></li>
     *   <li>Longitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>138.0512689</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-15 : FORWARD")
    public void GIGS_5207_15() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-27.125, 138.05};
        final double[] destinationPoint = new double[]{-27.12352361, 138.0512689};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-16” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-16</b></li>
     *   <li>Latitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>-27.12647639</b></li>
     *   <li>Longitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>138.0487311</b></li>
     *   <li>Latitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>-27.125</b></li>
     *   <li>Longitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>138.05</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-16 : REVERSE")
    public void GIGS_5207_16() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-27.125, 138.05};
        final double[] destinationPoint = new double[]{-27.12647639, 138.0487311};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-17” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-17</b></li>
     *   <li>Latitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>-27.125</b></li>
     *   <li>Longitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>138.055</b></li>
     *   <li>Latitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>-27.12352333</b></li>
     *   <li>Longitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>138.0562689</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-17 : FORWARD")
    public void GIGS_5207_17() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-27.125, 138.055};
        final double[] destinationPoint = new double[]{-27.12352333, 138.0562689};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-18” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-18</b></li>
     *   <li>Latitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>-27.12647667</b></li>
     *   <li>Longitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>138.0587311</b></li>
     *   <li>Latitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>-27.125</b></li>
     *   <li>Longitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>138.06</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-18 : REVERSE")
    public void GIGS_5207_18() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-27.125, 138.06};
        final double[] destinationPoint = new double[]{-27.12647667, 138.0587311};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-19” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-19</b></li>
     *   <li>Latitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>-28.05</b></li>
     *   <li>Longitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>136.8472222</b></li>
     *   <li>Latitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>-28.04852694</b></li>
     *   <li>Longitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>136.8485253</b></li>
     *   <li>Transect: <b>D</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-19 : REVERSE")
    public void GIGS_5207_19() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-28.04852694, 136.8485253};
        final double[] destinationPoint = new double[]{-28.05, 136.8472222};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-20” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-20</b></li>
     *   <li>Latitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>-28.05</b></li>
     *   <li>Longitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>137.8472222</b></li>
     *   <li>Latitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>-28.04853111</b></li>
     *   <li>Longitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>137.8485056</b></li>
     *   <li>Transect: <b>D</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-20 : FORWARD")
    public void GIGS_5207_20() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-28.05, 137.8472222};
        final double[] destinationPoint = new double[]{-28.04853111, 137.8485056};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-21” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-21</b></li>
     *   <li>Latitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>-28.05</b></li>
     *   <li>Longitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>138.8472222</b></li>
     *   <li>Latitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>-28.04851889</b></li>
     *   <li>Longitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>138.8484925</b></li>
     *   <li>Transect: <b>D</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-21 : FORWARD")
    public void GIGS_5207_21() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-28.05, 138.8472222};
        final double[] destinationPoint = new double[]{-28.04851889, 138.8484925};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-22” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-22</b></li>
     *   <li>Latitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>-28.05</b></li>
     *   <li>Longitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202): <b>139.8472222</b></li>
     *   <li>Latitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>-28.04850722</b></li>
     *   <li>Longitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283): <b>139.8484753</b></li>
     *   <li>Transect: <b>D</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-22 : REVERSE")
    public void GIGS_5207_22() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-28.04850722, 139.8484753};
        final double[] destinationPoint = new double[]{-28.05, 139.8472222};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }
}
