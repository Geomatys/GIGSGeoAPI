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

import org.iogp.gigs.internal.geoapi.Pending;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CSFactory;
import org.opengis.referencing.datum.DatumFactory;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.OperationMethod;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;


/**
 * Verifies the software’s capabilities to perform coordinate transformations for the Geocentric Translation method
 * which operate ‘from’ and ‘to’ geographic 3D coordinate reference systems.
 *
 * <table class="gigs">
 * <caption>Test description</caption>
 * <tr>
 *   <th>Test method:</th>
 *   <td><ul>
 *      <li>Invoke GIGS transformation “GIGS geogCRS B to GIGS geogCRS A (1)”, GIGS code 61196 in both
 *      directions and inspect results.</li>
 *      <li>For the first test point perform iterations of forward and reverse computations using the output
 *      of computation n as input into computation n+1, until the output coordinate values exceed more
 *      than 0.006m (6mm) or 0.00000006° in horizontal and vertical from the original calculated values
 *      (before 1000 iterations).</li></ul>
 *   </td>
 * </tr><tr>
 *   <th>Test data folder:</th>
 *   <td><a href="https://github.com/IOGP-GIGS/GIGSTestDataset/blob/main/GIGSTestDatasetFiles/GIGS%205200%20Coordinate%20transformation%20test%20data/ASCII/GIGS_tfm_5212_3trnslt_Geog3D_input.txt">{@code GIGS_tfm_5212_3trnslt_Geog3D_input.txt}</a>
 * </tr><tr>
 *   <th>Tested API:</th>
 *   <td>{@link CoordinateOperationFactory#createOperation(CoordinateReferenceSystem, CoordinateReferenceSystem, OperationMethod)}.</td>
 * </tr><tr>
 *   <th>Expected result:</th>
 *   <td>Results should agree to within 0.03m (30mm) or 0.0000003° horizontal and 0.01m (10mm)
 *      0.00000009° vertical of the Test Data. See files GIGS_tfm_5212_3trnslt_Geog3D_output_AbrMol
 *      or GIGS_tfm_5212_3trnslt_Geog3D_output_EPSGconcat. These two sets use alternative methods,
 *      both of which are valid for the transformation of geographic 3D CRSs. Report which of the
 *      coordinate transformation methods the application is using.
 *      <p>For the round trip calculation the initial calculated coordinates of the point should change by less
 *      than 0.006m (6mm) or 0.00000006° in horizontal and vertical (before 1000 iterations).
 *      <p>Test result will be pass or fail. If fail, details of failure should be reported.
 *      <p>Some applications ignore the ellipsoidal heights during these transformations (or set the input
 *      ellipsoidal heights to zero). If that is the case, horizontal coordinates obtained for those points
 *      with ellipsoidal heights significantly different from zero will be incorrect, whereas correct results
 *      may be generated for points with zero (or near zero) ellipsoidal heights. For large ellipsoidal
 *      heights (either positive or negative), the correct results are given by the geog3D EPSG method
 *      1035. Results that match should be clearly documented in the report on the test results. To assist
 *      this evaluation, the results file includes data generated using the Abridged Molodensky method..</td>
 * </tr></table>
 *
 *
 * <h2>Usage example</h2>
 * In order to specify their factories and run the tests in a JUnit framework, implementers can
 * define a subclass in their own test suite as in the example below:
 *
 * {@snippet lang = "java":
 * public class MyTest extends Test5212EPSGconcat {
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
@DisplayName("Geocentric translations (geographic 3D domain) transformation")
public class Test5212EPSGconcat extends Test5212 {

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
    public Test5212EPSGconcat(final Factories factories) throws FactoryException {
        super( factories);
    }

    @Override
    protected void createCoOp() throws FactoryException {
        final OperationMethod om = Pending.getOperationMethod(mtFactory, "Geocentric translations (geog3D domain)");
        forwardCoOp = copFactory.createOperation(geogCRSB, geogCRSA, om);
        reverseCoOp = copFactory.createOperation(geogCRSA, geogCRSB, om);
    }

    /**
     * Tests “GIGS-5212-01” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-01</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>79.99575759</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>150.0045612</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>1350.036</b></li>
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
    @DisplayName("GIGS-5212-01 : REVERSE")
    public void GIGS_5212_01() throws TransformException {
        isRTC = true;
        isForward = false;
        final double[] originPoint = new double[]{80, 150, 1214.137};
        final double[] destinationPoint = new double[]{79.99575759, 150.0045612, 1350.036};
        convertAndVerifyRoundTripPoint(originPoint, destinationPoint);
    }

    /**
     * Tests “GIGS-5212-02” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-02</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>79.99575679</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>150.0045621</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>135.899</b></li>
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
    @DisplayName("GIGS-5212-02 : REVERSE")
    public void GIGS_5212_02() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{80, 150, 0};
        final double[] destinationPoint = new double[]{79.99575679, 150.0045621, 135.899};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5212-03” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-03</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>60</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>120</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>900</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>60.00475191</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>119.9952454</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>619.632</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5212-03 : FORWARD")
    public void GIGS_5212_03() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{60, 120, 900};
        final double[] destinationPoint = new double[]{60.00475191, 119.9952454, 619.632};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5212-04” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-04</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>60</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>120</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>60.00475258</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>119.9952447</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-280.368</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5212-04 : FORWARD")
    public void GIGS_5212_04() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{60, 120, 0};
        final double[] destinationPoint = new double[]{60.00475258, 119.9952447, -280.368};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5212-05” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-05</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>29.99639863</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>60.0039103</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>450.048</b></li>
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
    @DisplayName("GIGS-5212-05 : REVERSE")
    public void GIGS_5212_05() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{30, 60, 189.569};
        final double[] destinationPoint = new double[]{29.99639863, 60.0039103, 450.048};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5212-06” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-06</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>29.99639852</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>60.00391042</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>260.479</b></li>
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
    @DisplayName("GIGS-5212-06 : REVERSE")
    public void GIGS_5212_06() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{30, 60, 0};
        final double[] destinationPoint = new double[]{29.99639852, 60.00391042, 260.479};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5212-07” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-07</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>0.00392509</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-0.00100615</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-202.588</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5212-07 : FORWARD")
    public void GIGS_5212_07() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{0, 0, 0};
        final double[] destinationPoint = new double[]{0.00392509, -0.00100615, -202.588};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5212-08” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-08</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-3000</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>0.00392695</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-0.00100662</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-3202.588</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5212-08 : FORWARD")
    public void GIGS_5212_08() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{0, 0, -3000};
        final double[] destinationPoint = new double[]{0.00392695, -0.00100662, -3202.588};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5212-09” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-09</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-10000</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>0.00393129</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-0.00100773</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-10202.588</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5212-09 : FORWARD")
    public void GIGS_5212_09() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{0, 0, -10000};
        final double[] destinationPoint = new double[]{0.00393129, -0.00100773, -10202.588};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5212-10” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-10</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-30.00405481</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-60.00274971</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>526.476</b></li>
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
    @DisplayName("GIGS-5212-10 : REVERSE")
    public void GIGS_5212_10() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-30, -60, 0};
        final double[] destinationPoint = new double[]{-30.00405481, -60.00274971, 526.476};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5212-11” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-11</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-30.00405515</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-60.00274993</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>0</b></li>
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
    @DisplayName("GIGS-5212-11 : REVERSE")
    public void GIGS_5212_11() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-30, -60, -526.476};
        final double[] destinationPoint = new double[]{-30.00405515, -60.00274993, 0};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5212-12” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-12</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-30.00405517</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-60.00274995</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-44.999</b></li>
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
    @DisplayName("GIGS-5212-12 : REVERSE")
    public void GIGS_5212_12() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-30, -60, -571.476};
        final double[] destinationPoint = new double[]{-30.00405517, -60.00274995, -44.999};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5212-13” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-13</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-60</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-120</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-59.99934884</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-119.9932376</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-935.1</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5212-13 : FORWARD")
    public void GIGS_5212_13() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-60, -120, 0};
        final double[] destinationPoint = new double[]{-59.99934884, -119.9932376, -935.1};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5212-14” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-14</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-60</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-120</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-900</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-59.99934874</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-119.9932366</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-1835.1</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5212-14 : FORWARD")
    public void GIGS_5212_14() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-60, -120, -900};
        final double[] destinationPoint = new double[]{-59.99934874, -119.9932366, -1835.1};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5212-15” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-15</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-79.99809461</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-150.014563</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>971.255</b></li>
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
    @DisplayName("GIGS-5212-15 : REVERSE")
    public void GIGS_5212_15() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-80, -150, 0};
        final double[] destinationPoint = new double[]{-79.99809461, -150.014563, 971.255};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5212-16” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-16</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-79.99809432</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-150.0145652</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>0</b></li>
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
    @DisplayName("GIGS-5212-16 : REVERSE")
    public void GIGS_5212_16() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-80, -150, -971.255};
        final double[] destinationPoint = new double[]{-79.99809432, -150.0145652, 0};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5212-17” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-17</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-79.99809363</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-150.0145706</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-2345</b></li>
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
    @DisplayName("GIGS-5212-17 : REVERSE")
    public void GIGS_5212_17() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-80, -150, -3316.255};
        final double[] destinationPoint = new double[]{-79.99809363, -150.0145706, -2345};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5212-18” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-18</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-180</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>70.00490733</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-179.9970662</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-223.618</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5212-18 : FORWARD")
    public void GIGS_5212_18() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{70, -180, 0};
        final double[] destinationPoint = new double[]{70.00490733, -179.9970662, -223.618};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5212-19” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-19</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>49.9955376</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-135.0047634</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>313.063</b></li>
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
    @DisplayName("GIGS-5212-19 : REVERSE")
    public void GIGS_5212_19() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{50, -135, 0};
        final double[] destinationPoint = new double[]{49.9955376, -135.0047634, 313.063};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5212-20” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-20</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>25</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-90</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>25.00366329</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-89.99632465</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-274.729</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5212-20 : FORWARD")
    public void GIGS_5212_20() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{25, -90, 0};
        final double[] destinationPoint = new double[]{25.00366329, -89.99632465, -274.729};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5212-21” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-21</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-0.0039251</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>0.00100617</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>202.62</b></li>
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
    @DisplayName("GIGS-5212-21 : REVERSE")
    public void GIGS_5212_21() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{0, 0, 0};
        final double[] destinationPoint = new double[]{-0.0039251, 0.00100617, 202.62};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5212-22” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-22</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-37.6532236</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>143.9279419</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>1836.947</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-37.65282218</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>143.9264925</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>737.718</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5212-22 : FORWARD")
    public void GIGS_5212_22() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-37.6532236, 143.9279419, 1836.947};
        final double[] destinationPoint = new double[]{-37.65282218, 143.9264925, 737.718};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5212-23” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-23</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-37.6532236</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>143.9279419</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-37.65282206</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>143.9264921</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-1099.229</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5212-23 : FORWARD")
    public void GIGS_5212_23() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-37.6532236, 143.9279419, 0};
        final double[] destinationPoint = new double[]{-37.65282206, 143.9264921, -1099.229};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5212-24” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-24</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-37.6532236</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>143.9279419</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-3000</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-37.65282187</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>143.9264914</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-4099.229</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5212-24 : FORWARD")
    public void GIGS_5212_24() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-37.6532236, 143.9279419, -3000};
        final double[] destinationPoint = new double[]{-37.65282187, 143.9264914, -4099.229};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5212-25” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-25</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-37.6532236</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>143.9279419</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-10000</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-37.65282143</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>143.9264898</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-11099.229</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5212-25 : FORWARD")
    public void GIGS_5212_25() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-37.6532236, 143.9279419, -10000};
        final double[] destinationPoint = new double[]{-37.65282143, 143.9264898, -11099.229};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5212-26” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-26</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-49.99946316</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>135.0025542</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>1079.777</b></li>
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
    @DisplayName("GIGS-5212-26 : REVERSE")
    public void GIGS_5212_26() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-50, 135, 0};
        final double[] destinationPoint = new double[]{-49.99946316, 135.0025542, 1079.777};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5212-27” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-27</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-70</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>180</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>0</b></li>
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-70.00224647</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-179.9970662</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-1039.29</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5212-27 : FORWARD")
    public void GIGS_5212_27() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-70, 180, 0};
        final double[] destinationPoint = new double[]{-70.00224647, -179.9970662, -1039.29};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }
}
