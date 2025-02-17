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

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 *  Verifies the software’s capabilities to perform transformations for Vertical Offset method.
 *
 * <table class="gigs">
 * <caption>Test description</caption>
 * <tr>
 *   <th>Test method:</th>
 *   <td><ul>
 *      <li>Invoke GIGS vertical transformations “GIGS vertCRS W1 height/depth to GIGS vertCRS V1 height/
 *      depth”, GIGS codes 65400, 65438, 65440 and 65441, in both directions</li>
 *      <li>For the first test point perform iterations of forward and reverse computations using the output
 *      of computation n as input into computation n+1, until the output coordinate values exceed more
 *      than 0.006m (6mm) from the original calculated values (before 1000 iterations).</li>
 *      <li>The order of transformations is discretionary, but the following is recommended:
 *          <ul><li>transform height referenced to “GIGS vertCRS W1” into depth referenced to “GIGS vertCRS W1”.</li>
 *          <li>transform height referenced to “GIGS vertCRS W1” into height referenced to “GIGS vertCRS V1”.</li>
 *          <li>transform height referenced to “GIGS vertCRS W1” into depth referenced to “GIGS vertCRS V1”.</li>
 *          <li>transform depth referenced to “GIGS vertCRS W1” into height referenced to “GIGS vertCRS W1”.</li>
 *          <li>transform depth referenced to “GIGS vertCRS W1” into height referenced to “GIGS vertCRS V1”.</li>
 *          <li>transform depth referenced to “GIGS vertCRS W1” into depth referenced to “GIGS vertCRS V1”.</li>
 *          <li>transform height referenced to “GIGS vertCRS V1” into height referenced to “GIGS vertCRS W1”.</li>
 *          <li>transform height referenced to “GIGS vertCRS V1” into depth referenced to “GIGS vertCRS W1”.</li>
 *          <li>transform height referenced to “GIGS vertCRS V1” into depth referenced to “GIGS vertCRS V1”.</li>
 *          <li>transform depth referenced to “GIGS vertCRS V1” into height referenced to “GIGS vertCRS W1”.</li>
 *          <li>transform depth referenced to “GIGS vertCRS V1” into depth referenced to “GIGS vertCRS W1”.</li>
 *          <li>transform depth referenced to “GIGS vertCRS V1” into height referenced to “GIGS vertCRS V1”.</li></ul>
 *      </li>
 *   </ul>
 *   </td>
 * </tr><tr>
 *   <th>Test data folder:</th>
 *   <td><a href="https://github.com/IOGP-GIGS/GIGSTestDataset/blob/main/GIGSTestDatasetFiles/GIGS%205200%20Coordinate%20transformation%20test%20data/ASCII/GIGS_tfm_5210_VertOff_input.txt">{@code GIGS_tfm_5210_VertOff_input.txt}</a>
 * </tr><tr>
 *   <th>Tested API:</th>
 *   <td>{@link CoordinateOperationFactory#createOperation(CoordinateReferenceSystem, CoordinateReferenceSystem)}.</td>
 * </tr><tr>
 *   <th>Expected result:</th>
 *   <td>Results should agree to within 0.01m (10mm) or 0.00000009° of the Test Data.
 *   <p>See file GIGS_tfm_5210_VertOff_output.
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
 * public class MyTest extends Test5210 {
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
@DisplayName("Vertical Offset")
public class Test5210 extends Series5000 {
    /**
     * Data about the CRS of the Vertical CRS W1 height.
     *
     * @see #createVertW1H(TestMethod)
     */
    final Test3210 vertW1HTest;
    /**
     * Data about the CRS of the Vertical CRS W1 depth.
     *
     * @see #createVertW1D(TestMethod)
     */
    final Test3210 vertW1DTest;
    /**
     * Data about the CRS of the Vertical CRS V1 height.
     *
     * @see #createVertV1H(TestMethod)
     */
    final Test3210 vertV1HTest;
    /**
     * Data about the CRS of the Vertical CRS V1 depth.
     *
     * @see #createVertV1D(TestMethod)
     */
    final Test3210 vertV1DTest;

    /**
     * Data about the CRS of the geographic CRS.
     *
     * @see #createGeogCRSY(TestMethod)
     */
    final Test3205 geogCRSYTest;

    /**
     * The Vertical CRS V1 height created by this factory.
     */
    protected VerticalCRS vertW1H;

    /**
     * The Vertical CRS V1 depth created by this factory.
     */
    protected VerticalCRS vertW1D;

    /**
     * The Vertical CRS W1 height created by this factory.
     */
    protected VerticalCRS vertV1H;

    /**
     * The Vertical CRS W1 depth created by this factory.
     */
    protected VerticalCRS vertV1D;

    protected CompoundCRS vertW1HCompCRS;
    protected CompoundCRS vertW1DCompCRS;
    protected CompoundCRS vertV1HCompCRS;
    protected CompoundCRS vertV1DCompCRS;

    private CoordinateOperation operationW1HtoW1D;
    private CoordinateOperation operationW1HtoV1H;
    private CoordinateOperation operationW1HtoV1D;

    private CoordinateOperation operationW1DtoW1H;
    private CoordinateOperation operationW1DtoV1H;
    private CoordinateOperation operationW1DtoV1D;

    private CoordinateOperation operationV1HtoW1H;
    private CoordinateOperation operationV1HtoW1D;
    private CoordinateOperation operationV1HtoV1D;

    private CoordinateOperation operationV1DtoW1H;
    private CoordinateOperation operationV1DtoW1D;
    private CoordinateOperation operationV1DtoV1H;

    /**
     * The geographic CRS created by the factory,
     * or {@code null} if not yet created or if geographic CRS creation failed.
     */
    protected GeographicCRS geogCRSY;

    /**
     * Factory to use for building {@link Conversion} instances, or {@code null} if none.
     * This is the factory used by the {@link #convertPoint(double[])} ()} method.
     */
    protected final CoordinateOperationFactory copFactory;

    /**
     * Factory to use for building {@link org.opengis.referencing.crs.CompoundCRS} instances, or {@code null} if none.
     * This is the factory used by the {@link #createCRSs()} ()} ()} method.
     */
    protected final CRSFactory crsFactory;

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
    public Test5210(final Factories factories) throws FactoryException {
        super( 0.01, 0.00000009, 0.006, 0.00000006);
        copFactory = factories.copFactory;
        crsFactory = factories.crsFactory;

        vertW1HTest = new Test3210(factories);
        vertW1HTest.skipTests = true;
        vertW1HTest.skipIdentificationCheck = true;

        vertW1DTest = new Test3210(factories);
        vertW1DTest.skipTests = true;
        vertW1DTest.skipIdentificationCheck = true;

        vertV1HTest = new Test3210(factories);
        vertV1HTest.skipTests = true;
        vertV1HTest.skipIdentificationCheck = true;

        vertV1DTest = new Test3210(factories);
        vertV1DTest.skipTests = true;
        vertV1DTest.skipIdentificationCheck = true;

        geogCRSYTest = new Test3205(factories);
        geogCRSYTest.skipTests = true;
        geogCRSYTest.skipIdentificationCheck = true;

        createCRSs();
        createCompoundCRS();
        createCoOp();
    }

    /**
     * Method used in class instantiation to initialise the geographic CRS E and the geographic CRS A
     * used in this test class.
     * <p>Geographic CRS Y: GIGS CRS Code 64017; GIGS geogCRS Y; Pulkovo 1942; decimal degree; EPSG CRS code 4284
     * <p>Vertical CRS W1 height: GIGS CRS Code 64507; GIGS vertCRS W1 height; Caspian height; metre; EPSG CRS code 5611
     * <p>Vertical CRS W1 depth: GIGS CRS Code 64508; GIGS vertCRS W1 depth; Caspian depth; metre; EPSG CRS code 5706
     * <p>Vertical CRS V1 height: GIGS CRS Code 64505; GIGS vertCRS V1 height; Baltic 1977 height; metre; EPSG CRS code 5705
     * <p>Vertical CRS V1 depth: GIGS CRS Code 64506; GIGS vertCRS V1 depth; Baltic 1977 depth; metre; EPSG CRS code 5612
     *
     * @throws FactoryException if an error occurred while creating a CRS.
     */
    @Override
    protected void createCRSs() throws FactoryException {
        createGeogCRSY(Test3205::GIGS_64017);
        createVertW1H(Test3210::GIGS_64507);
        createVertW1D(Test3210::GIGS_64508);
        createVertV1H(Test3210::GIGS_64505);
        createVertV1D(Test3210::GIGS_64506);
    }

    @Override
    protected void createCoOp() throws FactoryException {
        operationW1HtoW1D = copFactory.createOperation(vertW1HCompCRS, vertW1DCompCRS);
        operationW1HtoV1H = copFactory.createOperation(vertW1HCompCRS, vertV1HCompCRS);
        operationW1HtoV1D = copFactory.createOperation(vertW1HCompCRS, vertV1DCompCRS);

        operationW1DtoW1H = copFactory.createOperation(vertW1DCompCRS, vertW1HCompCRS);
        operationW1DtoV1H = copFactory.createOperation(vertW1DCompCRS, vertV1HCompCRS);
        operationW1DtoV1D = copFactory.createOperation(vertW1DCompCRS, vertV1DCompCRS);

        operationV1HtoW1H = copFactory.createOperation(vertV1HCompCRS, vertW1HCompCRS);
        operationV1HtoW1D = copFactory.createOperation(vertV1HCompCRS, vertW1DCompCRS);
        operationV1HtoV1D = copFactory.createOperation(vertV1HCompCRS, vertV1DCompCRS);

        operationV1DtoW1H = copFactory.createOperation(vertV1DCompCRS, vertW1HCompCRS);
        operationV1DtoW1D = copFactory.createOperation(vertV1DCompCRS, vertW1DCompCRS);
        operationV1DtoV1H = copFactory.createOperation(vertV1DCompCRS, vertV1HCompCRS);
    }

    /**
     * Creates a user-defined vertical CRS by executing the specified method from the {@link Test3210} class.
     *
     * @param  factory          the test method to use for creating the vertical CRS.
     * @throws FactoryException if an error occurred while creating the vertical CRS.
     */
    void createVertW1H(final TestMethod<Test3210> factory) throws FactoryException {
        factory.initialize(vertW1HTest);
        vertW1H = vertW1HTest.getIdentifiedObject();
    }

    /**
     * Creates a user-defined vertical CRS by executing the specified method from the {@link Test3210} class.
     *
     * @param  factory          the test method to use for creating the vertical CRS.
     * @throws FactoryException if an error occurred while creating the vertical CRS.
     */
    void createVertW1D(final TestMethod<Test3210> factory) throws FactoryException {
        factory.initialize(vertW1DTest);
        vertW1D = vertW1DTest.getIdentifiedObject();
    }

    /**
     * Creates a user-defined vertical CRS by executing the specified method from the {@link Test3210} class.
     *
     * @param  factory          the test method to use for creating the vertical CRS.
     * @throws FactoryException if an error occurred while creating the vertical CRS.
     */
    void createVertV1H(final TestMethod<Test3210> factory) throws FactoryException {
        factory.initialize(vertV1HTest);
        vertV1H = vertV1HTest.getIdentifiedObject();
    }

    /**
     * Creates a user-defined vertical CRS by executing the specified method from the {@link Test3210} class.
     *
     * @param  factory          the test method to use for creating the vertical CRS.
     * @throws FactoryException if an error occurred while creating the vertical CRS.
     */
    void createVertV1D(final TestMethod<Test3210> factory) throws FactoryException {
        factory.initialize(vertV1DTest);
        vertV1D = vertV1DTest.getIdentifiedObject();
    }

    /**
     * Creates a user-defined geographic CRS by executing the specified method from the {@link Test3205} class.
     *
     * @param  factory          the test method to use for creating the geographic CRS.
     * @throws FactoryException if an error occurred while creating the geographic CRS.
     */
    void createGeogCRSY(final TestMethod<Test3205> factory) throws FactoryException {
        factory.initialize(geogCRSYTest);
        geogCRSY = (GeographicCRS) geogCRSYTest.getIdentifiedObject();
    }

    /**
     * Creates a Compound CRS by executing the specified method from the {@link CRSFactory} class.
     *
     */
    void createCompoundCRS() throws FactoryException {
        vertW1HCompCRS = crsFactory.createCompoundCRS(Collections.singletonMap("name", "vertW1H"), geogCRSY, vertW1H);
        vertW1DCompCRS = crsFactory.createCompoundCRS(Collections.singletonMap("name", "vertW1D"), geogCRSY, vertW1D);
        vertV1HCompCRS = crsFactory.createCompoundCRS(Collections.singletonMap("name", "vertV1H"), geogCRSY, vertV1H);
        vertV1DCompCRS = crsFactory.createCompoundCRS(Collections.singletonMap("name", "vertV1D"), geogCRSY, vertV1D);

    }

    /**
     * Verifies the result of the conversion produced by {@link #convertPoint(double[])} ()}.
     *
     * @param expectedPoint the expected coordinates from the GIGS output file.
     * @param resPoint the coordinates computed by the factory.
     */
    @Override
    void verifyConversion(double[] expectedPoint, double[] resPoint) {
        return;
    }

    /**
     * Verifies the result of the conversion produced by {@link #convertPoint(double[])} ()}.
     *
     * @param expectedPoint the expected coordinates from the GIGS output file.
     * @param resPoint the coordinates computed by the factory.
     */
    void verifyConversion(double expectedPoint, double resPoint) {
        assertEquals(expectedPoint, resPoint, isRTC ? rtCartTolerance : cartTolerance);
    }


    /**
     * Performs a round trip conversion
     *
     * @param operation1 the operation to use to convert/transform the point coordinates from origin CRS.
     * @param operation2 the operation to use to convert/transform the point coordinates back to origin CRS.
     * @param originPoint the point to convert to the destination CRS.
     * @param destinationPoint the expected coordinates from the GIGS output file.
     * @throws TransformException if an error occurred while converting the given point.
     */
    void convertAndVerifyRoundTripPoint(final CoordinateOperation operation1, final CoordinateOperation operation2, final double[] originPoint, final double destinationPoint) throws TransformException {
        double[] res = originPoint;
        int n = 0;
        while (n < 1000) {
            res = convertPoint(operation1, res);
            verifyConversion(destinationPoint, res[2]);
            n++;
            res = convertPoint(operation2, res);
            n++;
        }
        System.out.println("n = " + n);
    }

    /**
     * Tests “GIGS-5210-01”  for VertOff transformation.
     * <ul>
     *   <li>Point: <b>GIGS-5210-01</b></li>
     *   <li>Latitude (GIGS CRS Code 64017; GIGS geogCRS Y; Pulkovo 1942; decimal degree; EPSG CRS code 4284): <b>41.630305</b></li>
     *   <li>Longitude (GIGS CRS Code 64017; GIGS geogCRS Y; Pulkovo 1942; decimal degree; EPSG CRS code 4284): <b>50.609559</b></li>
     *   <li>Height (GIGS CRS Code 64507; GIGS vertCRS W1 height; Caspian height; metre; EPSG CRS code 5611): <b>100</b></li>
     *   <li>Depth (GIGS CRS Code 64508; GIGS vertCRS W1 depth; Caspian depth; metre; EPSG CRS code 5706): <b>-100</b></li>
     *   <li>Height (GIGS CRS Code 64505; GIGS vertCRS V1 height; Baltic 1977 height; metre; EPSG CRS code 5705): <b>72</b></li>
     *   <li>Depth (GIGS CRS Code 64506; GIGS vertCRS V1 depth; Baltic 1977 depth; metre; EPSG CRS code 5612): <b>-72</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: Round Trip calculation point.
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5210-01")
    public void GIGS_5210_01() throws TransformException, FactoryException {
        isRTC = true;
        isForward = false;
        final double[] originPoint = new double[]{41.630305, 50.609559, 72};
        CoordinateOperation operationR;
        CoordinateOperation operationF;
        operationR = copFactory.createOperation(vertV1HCompCRS, vertW1HCompCRS);
        operationF = copFactory.createOperation(vertW1HCompCRS, vertV1HCompCRS);
        convertAndVerifyRoundTripPoint(operationR, operationF, originPoint, 100);
        operationR = copFactory.createOperation(vertV1HCompCRS, vertW1DCompCRS);
        operationF = copFactory.createOperation(vertW1DCompCRS, vertV1HCompCRS);
        convertAndVerifyRoundTripPoint(operationR, operationF, originPoint, -100);
        operationR = copFactory.createOperation(vertV1HCompCRS, vertV1DCompCRS);
        operationF = copFactory.createOperation(vertV1DCompCRS, vertV1HCompCRS);
        convertAndVerifyRoundTripPoint(operationR, operationF, originPoint, -72);
    }

    /**
     * Tests “GIGS-5210-02”  for VertOff transformation.
     * <ul>
     *   <li>Point: <b>GIGS-5210-02</b></li>
     *   <li>Latitude (GIGS CRS Code 64017; GIGS geogCRS Y; Pulkovo 1942; decimal degree; EPSG CRS code 4284): <b>41.263119</b></li>
     *   <li>Longitude (GIGS CRS Code 64017; GIGS geogCRS Y; Pulkovo 1942; decimal degree; EPSG CRS code 4284): <b>50.609559</b></li>
     *   <li>Height (GIGS CRS Code 64507; GIGS vertCRS W1 height; Caspian height; metre; EPSG CRS code 5611): <b>94.67</b></li>
     *   <li>Depth (GIGS CRS Code 64508; GIGS vertCRS W1 depth; Caspian depth; metre; EPSG CRS code 5706): <b>-94.67</b></li>
     *   <li>Height (GIGS CRS Code 64505; GIGS vertCRS V1 height; Baltic 1977 height; metre; EPSG CRS code 5705): <b>66.67</b></li>
     *   <li>Depth (GIGS CRS Code 64506; GIGS vertCRS V1 depth; Baltic 1977 depth; metre; EPSG CRS code 5612): <b>-66.67</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5210-02")
    public void GIGS_5210_02() throws TransformException, FactoryException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{41.263119, 50.609559, 94.67};
        CoordinateOperation operationR;
        CoordinateOperation operationF;
        operationR = copFactory.createOperation(vertV1DCompCRS, vertW1HCompCRS);
        operationF = copFactory.createOperation(vertW1HCompCRS, vertV1DCompCRS);
        convertAndVerifyRoundTripPoint(operationR, operationF, originPoint, 100);
        operationR = copFactory.createOperation(vertV1DCompCRS, vertW1DCompCRS);
        operationF = copFactory.createOperation(vertW1DCompCRS, vertV1DCompCRS);
        convertAndVerifyRoundTripPoint(operationR, operationF, originPoint, -100);
        operationR = copFactory.createOperation(vertV1DCompCRS, vertV1HCompCRS);
        operationF = copFactory.createOperation(vertV1HCompCRS, vertV1DCompCRS);
        convertAndVerifyRoundTripPoint(operationR, operationF, originPoint, -72);
    }
}
