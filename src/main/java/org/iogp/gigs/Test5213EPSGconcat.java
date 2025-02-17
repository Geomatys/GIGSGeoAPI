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
 * Verifies the software’s capabilities to perform coordinate transformations for the geocentric translation method which operate
 * ‘from’ and ‘to’ geographic 2D coordinate reference systems.
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
 *      than 0.00000006° from the original calculated values (but no more than 1000 iterations).</li></ul>
 *   </td>
 * </tr><tr>
 *   <th>Test data folder:</th>
 *   <td><a href="https://github.com/IOGP-GIGS/GIGSTestDataset/blob/main/GIGSTestDatasetFiles/GIGS%205200%20Coordinate%20transformation%20test%20data/ASCII/GIGS_tfm_5213_3trnslt_Geog2D_input.txt">{@code GIGS_tfm_5213_3trnslt_Geog2D_input.txt}</a>
 * </tr><tr>
 *   <th>Tested API:</th>
 *   <td>{@link CoordinateOperationFactory#createOperation(CoordinateReferenceSystem, CoordinateReferenceSystem, OperationMethod)}.</td>
 * </tr><tr>
 *   <th>Expected result:</th>
 *   <td>One of two possible sets of results should agree to within 0.03m (30mm) or 0.0000003° of the
 *      Test Data. See file GIGS_tfm_5213_3trnslt_Geog2D_output_AbrMol or GIGS_tfm_5213_3trnslt_
 *      Geog2D_output_EPSGconcat. These two sets use alternative methods, both of which are valid
 *      for the transformation of geographic 2D CRSs. Report which of the coordinate transformation
 *      methods the application is using.
 *      <p>For the round trip calculation the initial calculated coordinates of the point should change by less
 *      than 0.006m (6mm) or 0.00000006° (before 1000 iterations).
 *      <p>Test result will be pass or fail. If fail, details of failure should be reported.</td>
 * </tr></table>
 *
 *
 * <h2>Usage example</h2>
 * In order to specify their factories and run the tests in a JUnit framework, implementers can
 * define a subclass in their own test suite as in the example below:
 *
 * {@snippet lang = "java":
 * public class MyTest extends Test5213EPSGconcat {
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
@DisplayName("Geocentric translations (geographic 2D domain) transformation")
public class Test5213EPSGconcat extends Test5213 {

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
    public Test5213EPSGconcat(final Factories factories) throws FactoryException {
        super(factories);
    }

    @Override
    protected void createCoOp() throws FactoryException {
        final OperationMethod om = Pending.getOperationMethod(mtFactory, "Geocentric translations (geog3D domain)");
        forwardCoOp = copFactory.createOperation(geogCRSB, geogCRSA, om);
        reverseCoOp = copFactory.createOperation(geogCRSA, geogCRSB, om);
    }

    /**
     * Tests “GIGS-5213-01” for Geocentric translation calculation outputs based on Abridged Molodensky (EPSG code 9605).
     *
     * <ul>
     *   <li>Point: <b>GIGS-5213-01</b></li>
     *   <li>Latitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>79.99575679</b></li>
     *   <li>Longitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>150.0045621</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>80</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>150</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: Round Trip calculation point.
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5213-01 : REVERSE")
    public void GIGS_5213_01() throws TransformException {
        isRTC = true;
        isForward = false;
        final double[] originPoint = new double[]{80, 150};
        final double[] destinationPoint = new double[]{79.99575679, 150.0045621};
        convertAndVerifyRoundTripPoint(originPoint, destinationPoint);
    }

    /**
     * Tests “GIGS-5213-02” for Geocentric translation calculation outputs based on Abridged Molodensky (EPSG code 9605).
     *
     * <ul>
     *   <li>Point: <b>GIGS-5213-02</b></li>
     *   <li>Latitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>60</b></li>
     *   <li>Longitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>120</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>60.00475258</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>119.9952447</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5213-02 : FORWARD")
    public void GIGS_5213_02() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{60, 120};
        final double[] destinationPoint = new double[]{60.00475258, 119.9952447};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5213-03” for Geocentric translation calculation outputs based on Abridged Molodensky (EPSG code 9605).
     *
     * <ul>
     *   <li>Point: <b>GIGS-5213-03</b></li>
     *   <li>Latitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>29.99639852</b></li>
     *   <li>Longitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>60.00391042</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>30</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>60</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5213-03 : REVERSE")
    public void GIGS_5213_03() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{30, 60};
        final double[] destinationPoint = new double[]{29.99639852, 60.00391042};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5213-04” for Geocentric translation calculation outputs based on Abridged Molodensky (EPSG code 9605).
     *
     * <ul>
     *   <li>Point: <b>GIGS-5213-04</b></li>
     *   <li>Latitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>0</b></li>
     *   <li>Longitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>0</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>0.00392509</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-0.00100615</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5213-04 : FORWARD")
    public void GIGS_5213_04() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{0, 0};
        final double[] destinationPoint = new double[]{0.00392509, -0.00100615};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5213-05” for Geocentric translation calculation outputs based on Abridged Molodensky (EPSG code 9605).
     *
     * <ul>
     *   <li>Point: <b>GIGS-5213-05</b></li>
     *   <li>Latitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>-30.00405481</b></li>
     *   <li>Longitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>-60.00274971</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-30</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-60</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5213-05 : REVERSE")
    public void GIGS_5213_05() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-30, -60};
        final double[] destinationPoint = new double[]{-30.00405481, -60.00274971};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5213-06” for Geocentric translation calculation outputs based on Abridged Molodensky (EPSG code 9605).
     *
     * <ul>
     *   <li>Point: <b>GIGS-5213-06</b></li>
     *   <li>Latitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>-60</b></li>
     *   <li>Longitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>-120</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-59.99934884</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-119.9932376</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5213-06 : FORWARD")
    public void GIGS_5213_06() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-60, -120};
        final double[] destinationPoint = new double[]{-59.99934884, -119.9932376};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5213-07” for Geocentric translation calculation outputs based on Abridged Molodensky (EPSG code 9605).
     *
     * <ul>
     *   <li>Point: <b>GIGS-5213-07</b></li>
     *   <li>Latitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>-79.99809461</b></li>
     *   <li>Longitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>-150.014563</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-80</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-150</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5213-07 : REVERSE")
    public void GIGS_5213_07() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-80, -150};
        final double[] destinationPoint = new double[]{-79.99809461, -150.014563};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5213-08” for Geocentric translation calculation outputs based on Abridged Molodensky (EPSG code 9605).
     *
     * <ul>
     *   <li>Point: <b>GIGS-5213-08</b></li>
     *   <li>Latitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>70</b></li>
     *   <li>Longitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>-180</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>70.00490733</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-179.9970662</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5213-08 : FORWARD")
    public void GIGS_5213_08() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{70, -180};
        final double[] destinationPoint = new double[]{70.00490733, -179.9970662};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5213-09” for Geocentric translation calculation outputs based on Abridged Molodensky (EPSG code 9605).
     *
     * <ul>
     *   <li>Point: <b>GIGS-5213-09</b></li>
     *   <li>Latitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>49.9955376</b></li>
     *   <li>Longitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>-135.0047634</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>50</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-135</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5213-09 : REVERSE")
    public void GIGS_5213_09() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{50, -135};
        final double[] destinationPoint = new double[]{49.9955376, -135.0047634};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5213-10” for Geocentric translation calculation outputs based on Abridged Molodensky (EPSG code 9605).
     *
     * <ul>
     *   <li>Point: <b>GIGS-5213-10</b></li>
     *   <li>Latitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>25</b></li>
     *   <li>Longitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>-90</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>25.00366329</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-89.99632465</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5213-10 : FORWARD")
    public void GIGS_5213_10() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{25, -90};
        final double[] destinationPoint = new double[]{25.00366329, -89.99632465};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5213-11” for Geocentric translation calculation outputs based on Abridged Molodensky (EPSG code 9605).
     *
     * <ul>
     *   <li>Point: <b>GIGS-5213-11</b></li>
     *   <li>Latitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>-0.0039251</b></li>
     *   <li>Longitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>0.00100617</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>0</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>0</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5213-11 : REVERSE")
    public void GIGS_5213_11() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{0, 0};
        final double[] destinationPoint = new double[]{-0.0039251, 0.00100617};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5213-12” for Geocentric translation calculation outputs based on Abridged Molodensky (EPSG code 9605).
     *
     * <ul>
     *   <li>Point: <b>GIGS-5213-12</b></li>
     *   <li>Latitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>-37.6532236</b></li>
     *   <li>Longitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>143.9279419</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-37.65282206</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>143.9264921</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5213-12 : FORWARD")
    public void GIGS_5213_12() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-37.6532236, 143.9279419};
        final double[] destinationPoint = new double[]{-37.65282206, 143.9264921};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5213-13” for Geocentric translation calculation outputs based on Abridged Molodensky (EPSG code 9605).
     *
     * <ul>
     *   <li>Point: <b>GIGS-5213-13</b></li>
     *   <li>Latitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>-49.99946316</b></li>
     *   <li>Longitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>135.0025542</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-50</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>135</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5213-13 : REVERSE")
    public void GIGS_5213_13() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-50, 135};
        final double[] destinationPoint = new double[]{-49.99946316, 135.0025542};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5213-14” for Geocentric translation calculation outputs based on Abridged Molodensky (EPSG code 9605).
     *
     * <ul>
     *   <li>Point: <b>GIGS-5213-14</b></li>
     *   <li>Latitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>-70</b></li>
     *   <li>Longitude (GIGS CRS Code 64005; GIGS geogCRS B; OSGB36; decimal degree; EPSG CRS code 4277): <b>180</b></li>
     *   <li>Latitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-70.00224647</b></li>
     *   <li>Longitude (GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326): <b>-179.9970662</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Transformation direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while transforming the original point.
     */
    @Test
    @DisplayName("GIGS-5213-14 : FORWARD")
    public void GIGS_5213_14() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-70, 180};
        final double[] destinationPoint = new double[]{-70.00224647, -179.9970662};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }
}
