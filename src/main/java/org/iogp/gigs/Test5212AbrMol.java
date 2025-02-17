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
 * public class MyTest extends Test5212AbrMol {
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
public class Test5212AbrMol extends Test5212 {

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
    public Test5212AbrMol(final Factories factories) throws FactoryException {
        super( factories);
    }

    @Override
    protected void createCoOp() throws FactoryException {
        final OperationMethod om = Pending.getOperationMethod(mtFactory, "Abridged Molodensky");
        forwardCoOp = copFactory.createOperation(geogCRSB, geogCRSA, om);
        reverseCoOp = copFactory.createOperation(geogCRSA, geogCRSB, om);
    }

    /**
     * Tests “GIGS-5212-01” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-01</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>79.99575788</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>150.0045637</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>1350.003</b></li>
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
        final double[] destinationPoint = new double[]{79.99575788, 150.0045637, 1350.003};
        convertAndVerifyRoundTripPoint(originPoint, destinationPoint);
    }

    /**
     * Tests “GIGS-5212-02” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-02</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>79.99575788</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>150.0045637</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>135.866</b></li>
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
        final double[] destinationPoint = new double[]{79.99575788, 150.0045637, 135.866};
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
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>60.00475184</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>119.9952451</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>619.648</b></li>
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
        final double[] destinationPoint = new double[]{60.00475184, 119.9952451, 619.648};
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
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>60.00475184</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>119.9952451</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-280.352</b></li>
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
        final double[] destinationPoint = new double[]{60.00475184, 119.9952451, -280.352};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5212-05” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-05</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>29.99639764</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>60.00391035</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>449.974</b></li>
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
        final double[] destinationPoint = new double[]{29.99639764, 60.00391035, 449.974};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5212-06” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-06</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>29.99639764</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>60.00391035</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>260.405</b></li>
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
        final double[] destinationPoint = new double[]{29.99639764, 60.00391035, 260.405};
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
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>0.00392522</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-0.0010062</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-202.604</b></li>
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
        final double[] destinationPoint = new double[]{0.00392522, -0.0010062, -202.604};
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
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>0.00392522</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-0.0010062</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-3202.604</b></li>
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
        final double[] destinationPoint = new double[]{0.00392522, -0.0010062, -3202.604};
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
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>0.00392522</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-0.0010062</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-10202.604</b></li>
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
        final double[] destinationPoint = new double[]{0.00392522, -0.0010062, -10202.604};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5212-10” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-10</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-30.00405381</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-60.00274957</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>526.405</b></li>
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
        final double[] destinationPoint = new double[]{-30.00405381, -60.00274957, 526.405};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5212-11” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-11</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-30.00405381</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-60.00274957</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-0.071</b></li>
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
        final double[] destinationPoint = new double[]{-30.00405381, -60.00274957, -0.071};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5212-12” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-12</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-30.00405381</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-60.00274957</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-45.071</b></li>
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
        final double[] destinationPoint = new double[]{-30.00405381, -60.00274957, -45.071};
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
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-59.99934798</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-119.9932378</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-935.067</b></li>
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
        final double[] destinationPoint = new double[]{-59.99934798, -119.9932378, -935.067};
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
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-59.99934798</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-119.9932378</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-1835.068</b></li>
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
        final double[] destinationPoint = new double[]{-59.99934798, -119.9932378, -1835.068};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5212-15” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-15</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-79.99809556</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-150.0145665</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>971.231</b></li>
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
        final double[] destinationPoint = new double[]{-79.99809556, -150.0145665, 971.231};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5212-16” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-16</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-79.99809556</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-150.0145665</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-0.024</b></li>
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
        final double[] destinationPoint = new double[]{-79.99809556, -150.0145665, -0.024};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5212-17” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-17</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-79.99809556</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-150.0145665</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>-2345.024</b></li>
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
        final double[] destinationPoint = new double[]{-79.99809556, -150.0145665, -2345.024};
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
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>70.00490648</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-179.9970667</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-223.621</b></li>
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
        final double[] destinationPoint = new double[]{70.00490648, -179.9970667, -223.621};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5212-19” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-19</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>49.9955382</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-135.0047636</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>312.968</b></li>
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
        final double[] destinationPoint = new double[]{49.9955382, -135.0047636, 312.968};
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
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>25.00366455</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-89.99632458</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-274.716</b></li>
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
        final double[] destinationPoint = new double[]{25.00366455, -89.99632458, -274.716};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5212-21” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-21</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-0.00392496</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>0.00100611</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>202.604</b></li>
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
        final double[] destinationPoint = new double[]{-0.00392496, 0.00100611, 202.604};
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
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-37.65282262</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>143.9264922</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>737.775</b></li>
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
        final double[] destinationPoint = new double[]{-37.65282262, 143.9264922, 737.775};
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
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-37.65282262</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>143.9264922</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-1099.172</b></li>
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
        final double[] destinationPoint = new double[]{-37.65282262, 143.9264922, -1099.172};
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
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-37.65282262</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>143.9264922</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-4099.172</b></li>
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
        final double[] destinationPoint = new double[]{-37.65282262, 143.9264922, -4099.172};
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
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-37.65282262</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>143.9264922</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-11099.172</b></li>
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
        final double[] destinationPoint = new double[]{-37.65282262, 143.9264922, -11099.172};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5212-26” for Geocentric translation calculation outputs based on Abridged Molodensky.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5212-26</b></li>
     *   <li>Latitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>-49.99946348</b></li>
     *   <li>Longitude (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; decimal degree; No direct EPSG equivalent): <b>135.0025544</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64019; GIGS geog3DCRS B; OSGB36; metre; No direct EPSG equivalent): <b>1079.707</b></li>
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
        final double[] destinationPoint = new double[]{-49.99946348, 135.0025544, 1079.707};
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
     *   <li>Latitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>-70.00224516</b></li>
     *   <li>Longitude (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; decimal degree; EPSG CRS code 4979): <b>179.9970667</b></li>
     *   <li>Ellipsoidal height (GIGS CRS Code 64002; GIGS geog3DCRS A; WGS 84; metre; EPSG CRS code 4979): <b>-1039.275</b></li>
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
        final double[] destinationPoint = new double[]{-70.00224516, 179.9970667, -1039.275};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }
}
