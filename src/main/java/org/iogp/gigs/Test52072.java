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
 *   <td><a href="https://github.com/IOGP-GIGS/GIGSTestDataset/blob/main/GIGSTestDatasetFiles/GIGS%205200%20Coordinate%20transformation%20test%20data/ASCII/GIGS_tfm_5207_NTv2_input_part2.txt">{@code GIGS_tfm_5207_NTv2_input_part2.txt}</a>
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
 * public class MyTest extends Test52072 {
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
public class Test52072 extends Series5000 {
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
    public Test52072(final Factories factories) throws FactoryException {
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
     * Tests “GIGS-5207-23” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-23</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>NaN</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>NaN</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-143</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     *   <li>Value outside tfm grid; should fail</li>
     * </ul>
     *
     * Remarks: Round Trip calculation point;NAD83 position also used in NADCON test 5206.
     */
    @Test
    @DisplayName("GIGS-5207-23 : REVERSE")
    public void GIGS_5207_23() throws TransformException {
        isRTC = true;
        isForward = false;
        final double[] originPoint = new double[]{70, -143};
        Assertions.assertThrowsExactly(TransformException.class, () -> convertPoint(originPoint));
    }

    /**
     * Tests “GIGS-5207-24” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-24</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-142</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>69.99980694</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-142.0028047</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: NAD27 position also used in NADCON test 5206.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-24 : FORWARD")
    public void GIGS_5207_24() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{70, -142};
        final double[] destinationPoint = new double[]{69.99980694, -142.0028047};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-25” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-25</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>70.00019306</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-141.9971953</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-142</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NADCON test 5206.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-25 : REVERSE")
    public void GIGS_5207_25() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{70, -142};
        final double[] destinationPoint = new double[]{70.00019306, -141.9971953};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-26” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-26</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>70.00017917</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-140.9972197</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-141</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NADCON test 5206.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-26 : REVERSE")
    public void GIGS_5207_26() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{70, -141};
        final double[] destinationPoint = new double[]{70.00017917, -140.9972197};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-27” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-27</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-140</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>69.99982806</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-140.0027953</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: NAD27 position also used in NADCON test 5206.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-27 : FORWARD")
    public void GIGS_5207_27() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{70, -140};
        final double[] destinationPoint = new double[]{69.99982806, -140.0027953};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-28” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-28</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>70.0001586</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-138.9971995</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-139</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NADCON test 5206.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-28 : REVERSE")
    public void GIGS_5207_28() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{70, -139};
        final double[] destinationPoint = new double[]{70.0001586, -138.9971995};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-29” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-29</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-138</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>69.99987556</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-138.0029572</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: NAD27 position also used in NADCON test 5206.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-29 : FORWARD")
    public void GIGS_5207_29() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{70, -138};
        final double[] destinationPoint = new double[]{69.99987556, -138.0029572};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-30” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-30</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>70.00004639</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-134.9970819</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-135</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NADCON test 5206.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-30 : REVERSE")
    public void GIGS_5207_30() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{70, -135};
        final double[] destinationPoint = new double[]{70.00004639, -134.9970819};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-31” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-31</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-130</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>70.00005333</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-130.0027372</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: NAD27 position also used in NADCON test 5206.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-31 : FORWARD")
    public void GIGS_5207_31() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{70, -130};
        final double[] destinationPoint = new double[]{70.00005333, -130.0027372};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-32” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-32</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>69.99986639</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-128.0007347</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-128.0034722</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NADCON test 5206.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-32 : REVERSE")
    public void GIGS_5207_32() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{70, -128.0034722};
        final double[] destinationPoint = new double[]{69.99986639, -128.0007347};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-33” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-33</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-128</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>70.00013361</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-128.0027378</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: NAD27 position also used in NADCON test 5206.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-33 : FORWARD")
    public void GIGS_5207_33() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{70, -128};
        final double[] destinationPoint = new double[]{70.00013361, -128.0027378};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-34” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-34</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>69.99977806</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-125.9973883</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-126</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NADCON test 5206.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-34 : REVERSE")
    public void GIGS_5207_34() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{70, -126};
        final double[] destinationPoint = new double[]{69.99977806, -125.9973883};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-35” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-35</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-88.005575</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>70.00060944</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-88.0056825</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-35 : FORWARD")
    public void GIGS_5207_35() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{70, -88.005575};
        final double[] destinationPoint = new double[]{70.00060944, -88.0056825};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-36” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-36</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-88</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>70.00060917</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-88.00010722</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-36 : FORWARD")
    public void GIGS_5207_36() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{70, -88};
        final double[] destinationPoint = new double[]{70.00060917, -88.00010722};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-37” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-37</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>69.99939083</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-87.99989333</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-88</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-37 : REVERSE")
    public void GIGS_5207_37() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{70, -88};
        final double[] destinationPoint = new double[]{69.99939083, -87.99989333};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-38” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-38</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-87.6610917</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>70.00060333</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-87.66117167</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-38 : FORWARD")
    public void GIGS_5207_38() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{70, -87.6610917};
        final double[] destinationPoint = new double[]{70.00060333, -87.66117167};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-39” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-39</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>51.01695556</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-112.1656933</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>51.01700944</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-112.1666078</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-39 : REVERSE")
    public void GIGS_5207_39() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{51.01700944, -112.1666078};
        final double[] destinationPoint = new double[]{51.01695556, -112.1656933};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-40” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-40</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>50.99994917</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-111.9990939</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>51</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-112</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NADCON test 5206.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-40 : REVERSE")
    public void GIGS_5207_40() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{51, -112};
        final double[] destinationPoint = new double[]{50.99994917, -111.9990939};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-41” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-41</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>49.99995972</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-111.9990953</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>49.99999972</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-112</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NADCON test 5206.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-41 : REVERSE")
    public void GIGS_5207_41() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{49.99999972, -112};
        final double[] destinationPoint = new double[]{49.99995972, -111.9990953};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-42” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-42</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>49.99999972</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-112</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>50.00003967</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-112.0009046</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: NAD27 position also used in NADCON test 5206.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-42 : FORWARD")
    public void GIGS_5207_42() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{49.99999972, -112};
        final double[] destinationPoint = new double[]{50.00003967, -112.0009046};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-43” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-43</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>49.00000639</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-111.9991175</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>49</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-112</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NADCON test 5206.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-43 : REVERSE")
    public void GIGS_5207_43() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{49, -112};
        final double[] destinationPoint = new double[]{49.00000639, -111.9991175};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-44” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-44</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>48</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-112</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>47.99998889</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-112.0008525</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: NAD27 position also used in NADCON test 5206.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-44 : FORWARD")
    public void GIGS_5207_44() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{48, -112};
        final double[] destinationPoint = new double[]{47.99998889, -112.0008525};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-45” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-45</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>47.00000972</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-111.9991747</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>47</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-112</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NADCON test 5206.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-45 : REVERSE")
    public void GIGS_5207_45() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{47, -112};
        final double[] destinationPoint = new double[]{47.00000972, -111.9991747};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-46” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-46</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>47</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-112</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>46.99999028</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-112.0008253</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: NAD27 position also used in NADCON test 5206.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-46 : FORWARD")
    public void GIGS_5207_46() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{47, -112};
        final double[] destinationPoint = new double[]{46.99999028, -112.0008253};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-47” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-47</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>NaN</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>NaN</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>46</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-112</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     *   <li>Value outside tfm grid; should fail</li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NADCON test 5206.
     */
    @Test
    @DisplayName("GIGS-5207-47 : REVERSE")
    public void GIGS_5207_47() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{46, -112};
        Assertions.assertThrowsExactly(TransformException.class, () -> convertPoint(originPoint));
    }

    /**
     * Tests “GIGS-5207-48” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-48</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>46</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-112</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>NaN</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>NaN</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     *   <li>Value outside tfm grid; should fail</li>
     * </ul>
     */
    @Test
    @DisplayName("GIGS-5207-48 : FORWARD")
    public void GIGS_5207_48() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{46, -112};
        Assertions.assertThrowsExactly(TransformException.class, () -> convertPoint(originPoint));
    }

    /**
     * Tests “GIGS-5207-49” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-49</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>51</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-130.5177467</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>50.99977139</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-130.5194019</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: NAD27 position also used in NADCON test 5206.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-49 : FORWARD")
    public void GIGS_5207_49() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{51, -130.5177467};
        final double[] destinationPoint = new double[]{50.99977139, -130.5194019};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-50” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-50</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>51.00022861</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-130.5160917</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>51</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-130.5177467</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NADCON test 5206.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-50 : REVERSE")
    public void GIGS_5207_50() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{51, -130.5177467};
        final double[] destinationPoint = new double[]{51.00022861, -130.5160917};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-51” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-51</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>50.00024611</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-130.5160417</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>49.99999972</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-130.5177467</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NADCON test 5206.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-51 : REVERSE")
    public void GIGS_5207_51() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{49.99999972, -130.5177467};
        final double[] destinationPoint = new double[]{50.00024611, -130.5160417};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-52” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-52</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>49.99999972</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-130.5177467</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>49.99975333</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-130.5194517</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: NAD27 position also used in NADCON test 5206.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-52 : FORWARD")
    public void GIGS_5207_52() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{49.99999972, -130.5177467};
        final double[] destinationPoint = new double[]{49.99975333, -130.5194517};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-53” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-53</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>49.0002825</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-130.5160672</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>49</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-130.5177467</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NADCON test 5206.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-53 : REVERSE")
    public void GIGS_5207_53() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{49, -130.5177467};
        final double[] destinationPoint = new double[]{49.0002825, -130.5160672};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-54” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-54</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>48</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-130.5177467</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>47.99967944</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-130.5193911</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-54 : FORWARD")
    public void GIGS_5207_54() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{48, -130.5177467};
        final double[] destinationPoint = new double[]{47.99967944, -130.5193911};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-55” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-55</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>47.00036111</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-130.5161469</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>47</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-130.5177467</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NADCON test 5206.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-55 : REVERSE")
    public void GIGS_5207_55() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{47, -130.5177467};
        final double[] destinationPoint = new double[]{47.00036111, -130.5161469};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-56” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-56</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>47</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-130.5177467</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>46.99963861</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-130.5193464</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: NAD27 position also used in NADCON test 5206.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-56 : FORWARD")
    public void GIGS_5207_56() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{47, -130.5177467};
        final double[] destinationPoint = new double[]{46.99963861, -130.5193464};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-57” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-57</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>NaN</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>NaN</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>46</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-130.5177467</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     *   <li>Value outside tfm grid; should fail</li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NADCON test 5206.
     */
    @Test
    @DisplayName("GIGS-5207-57 : REVERSE")
    public void GIGS_5207_57() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{46, -130.5177467};
        Assertions.assertThrowsExactly(TransformException.class, () -> convertPoint(originPoint));
    }

    /**
     * Tests “GIGS-5207-58” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-58</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>45</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-130.5177467</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>NaN</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>NaN</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     *   <li>Value outside tfm grid; should fail</li>
     * </ul>
     *
     * Remarks: NAD27 position also used in NADCON test 5206.
     */
    @Test
    @DisplayName("GIGS-5207-58 : FORWARD")
    public void GIGS_5207_58() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{45, -130.5177467};
        Assertions.assertThrowsExactly(TransformException.class, () -> convertPoint(originPoint));
    }

    /**
     * Tests “GIGS-5207-59” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-59</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>48.11702639</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-132.9982053</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>48.11666667</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-133</b></li>
     *   <li>Transect: <b>D</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NADCON test 5206.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-59 : REVERSE")
    public void GIGS_5207_59() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{48.11666667, -133};
        final double[] destinationPoint = new double[]{48.11702639, -132.9982053};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-60” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-60</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>48.11666667</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-131.4555556</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>48.11633306</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-131.4572603</b></li>
     *   <li>Transect: <b>D</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: NAD27 position also used in NADCON test 5206.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-60 : FORWARD")
    public void GIGS_5207_60() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{48.11666667, -131.4555556};
        final double[] destinationPoint = new double[]{48.11633306, -131.4572603};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-61” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-61</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>48.11698278</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-130.5160981</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>48.11666667</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-130.5177467</b></li>
     *   <li>Transect: <b>D</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NADCON test 5206.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-61 : REVERSE")
    public void GIGS_5207_61() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{48.11666667, -130.5177467};
        final double[] destinationPoint = new double[]{48.11698278, -130.5160981};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-62” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-62</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>48.11666667</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-128.4555556</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>48.11639278</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-128.4570869</b></li>
     *   <li>Transect: <b>D</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: NAD27 position also used in NADCON test 5206.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-62 : FORWARD")
    public void GIGS_5207_62() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{48.11666667, -128.4555556};
        final double[] destinationPoint = new double[]{48.11639278, -128.4570869};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5207-63” for NTv2 transformation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5207-63</b></li>
     *   <li>Latitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>48.1167066</b></li>
     *   <li>Longitude (GIGS CRS Code 64012; GIGS geogCRS J; NAD27; decimal degree; EPSG CRS code 4267): <b>-128.5444</b></li>
     *   <li>Latitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>48.11643111</b></li>
     *   <li>Longitude (GIGS CRS Code 64018; GIGS geogCRS Z; NAD83; decimal degree; EPSG CRS code 4269): <b>-128.5459361</b></li>
     *   <li>Transect: <b>D</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: NAD83 position also used in NADCON test 5206.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5207-63 : REVERSE")
    public void GIGS_5207_63() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{48.11643111, -128.5459361};
        final double[] destinationPoint = new double[]{48.1167066, -128.5444};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }
}
