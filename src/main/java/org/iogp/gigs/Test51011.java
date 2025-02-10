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
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CSFactory;
import org.opengis.referencing.datum.DatumFactory;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;


/**
 * Verifies the software’s capabilities to perform conversions for the Transverse Mercator map projection.
 *
 * <table class="gigs">
 * <caption>Test description</caption>
 * <tr>
 *   <th>Test method:</th>
 *   <td><ul>
 *      <li>Invoke coordinate conversions in both directions and inspect results</li>
 *      <li>For the first test point in part 1, perform iterations of forward and reverse computations using the
 *      output of computation n as input into computation n+1, until the output coordinate values exceed
 *      more than 0.006m (6mm) or 0.00000006° from the original calculated values (but no more than
 *      1000 iterations).</li></ul>
 *   </td>
 * </tr><tr>
 *   <th>Test data folder:</th>
 *   <td><a href="https://github.com/IOGP-GIGS/GIGSTestDataset/blob/main/GIGSTestDatasetFiles/GIGS%205100%20Conversion%20test%20data/ASCII/GIGS_conv_5101_TM_input_part1.txt">{@code GIGS_conv_5101_TM_input_part1.txt}</a>
 * </tr><tr>
 *   <th>Tested API:</th>
 *   <td>{@link CoordinateOperationFactory#createOperation(CoordinateReferenceSystem, CoordinateReferenceSystem)}.</td>
 * </tr><tr>
 *   <th>Expected result:</th>
 *   <td>Results for the forward and reverse calculations should agree to within 0.03m (30mm) or
 *   0.0000003° of the Test Data. See file GIGS_conv_5101_TM_output_part1_JHS.
 *   For the round trip calculation the initial calculated coordinates of the point should change by less
 *   than 0.006m (6mm) or 0.00000006° (before 1000 iterations).
 *   Test result will be pass or fail. If fail, details of failure should be reported.</td>
 * </tr></table>
 *
 *
 * <h2>Usage example</h2>
 * In order to specify their factories and run the tests in a JUnit framework, implementers can
 * define a subclass in their own test suite as in the example below:
 * <p>
 * {@snippet lang = "java":
 * public class MyTest extends Test51011 {
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
@DisplayName("Transverse Mercator conversion")
public class Test51011 extends Series5100 {

    /**
     * Creates a new test using the given factories.
     * The factories needed by this class are {@link CRSFactory}, {@link CSFactory},
     * {@link DatumFactory}, {@link CoordinateOperationFactory}, {@link MathTransformFactory}
     * and {@link org.opengis.referencing.crs.CRSAuthorityFactory}.
     * If a requested factory is {@code null}, then the tests which depend on it will be skipped.
     *
     * @param factories  factories for creating the instances to test.
     *
     * @throws FactoryException if an error occurred while creating the CRSs.
     */
    public Test51011(final Factories factories) throws FactoryException {
        super(factories, 0.03, 0.0000003, 0.006, 0.00000006);
    }

    /**
     * Method used in class instantiation to initialise the geographic CRS and the projected CRS
     * used in this test class.
     * <p>Geographic CRS : GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326
     * <p>ProjectedCRS : GIGS CRS Code 62001; GIGS projCRS A1; WGS 84 / UTM zone 31N; metre; EPSG CRS code 32631
     *
     * @throws FactoryException if an error occurred while creating a CRS.
     */
    @Override
    protected void createCRSs() throws FactoryException {
        createGeogCRS(Test3205::GIGS_64003);
        createProjCRS(Test3207::GIGS_62007);
    }

    /**
     * Tests “GIGS-5101-01” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-01</b></li>
     *   <li>Latitude: <b>80</b></li>
     *   <li>Longitude: <b>3</b></li>
     *   <li>Easting: <b>496813.178</b></li>
     *   <li>Northing: <b>3358297.326</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: Round Trip calculation point.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-01 : FORWARD")
    public void GIGS_5101_01() throws TransformException {
        isRTC = true;
        isForward = true;
        final double[] originPoint = new double[]{80, 3};
        final double[] destinationPoint = new double[]{496813.178, 3358297.326};
        convertAndVerifyRoundTripPoint(originPoint, destinationPoint);
    }

    /**
     * Tests “GIGS-5101-02” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-02</b></li>
     *   <li>Latitude: <b>60</b></li>
     *   <li>Longitude: <b>3</b></li>
     *   <li>Easting: <b>678711.584</b></li>
     *   <li>Northing: <b>1134498.83</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-02 : REVERSE")
    public void GIGS_5101_02() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{678711.584, 1134498.83};
        final double[] destinationPoint = new double[]{60, 3};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-03” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-03</b></li>
     *   <li>Latitude: <b>49</b></li>
     *   <li>Longitude: <b>3</b></li>
     *   <li>Easting: <b>765648.501</b></li>
     *   <li>Northing: <b>-87944.74</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-03 : FORWARD")
    public void GIGS_5101_03() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{49, 3};
        final double[] destinationPoint = new double[]{765648.501, -87944.74};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-04” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-04</b></li>
     *   <li>Latitude: <b>40</b></li>
     *   <li>Longitude: <b>3</b></li>
     *   <li>Easting: <b>826893.845</b></li>
     *   <li>Northing: <b>-1087710.121</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-04 : REVERSE")
    public void GIGS_5101_04() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{826893.845, -1087710.121};
        final double[] destinationPoint = new double[]{40, 3};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-05” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-05</b></li>
     *   <li>Latitude: <b>20</b></li>
     *   <li>Longitude: <b>3</b></li>
     *   <li>Easting: <b>923539.353</b></li>
     *   <li>Northing: <b>-3308151.625</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-05 : FORWARD")
    public void GIGS_5101_05() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{20, 3};
        final double[] destinationPoint = new double[]{923539.353, -3308151.625};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-06” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-06</b></li>
     *   <li>Latitude: <b>0</b></li>
     *   <li>Longitude: <b>3</b></li>
     *   <li>Easting: <b>957087.829</b></li>
     *   <li>Northing: <b>-5527462.686</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-06 : REVERSE")
    public void GIGS_5101_06() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{957087.829, -5527462.686};
        final double[] destinationPoint = new double[]{0, 3};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-07” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-07</b></li>
     *   <li>Latitude: <b>-20</b></li>
     *   <li>Longitude: <b>3</b></li>
     *   <li>Easting: <b>923539.353</b></li>
     *   <li>Northing: <b>-7746773.748</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-07 : FORWARD")
    public void GIGS_5101_07() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-20, 3};
        final double[] destinationPoint = new double[]{923539.353, -7746773.748};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-08” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-08</b></li>
     *   <li>Latitude: <b>-40</b></li>
     *   <li>Longitude: <b>3</b></li>
     *   <li>Easting: <b>826893.845</b></li>
     *   <li>Northing: <b>-9967215.251</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-08 : REVERSE")
    public void GIGS_5101_08() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{826893.845, -9967215.251};
        final double[] destinationPoint = new double[]{-40, 3};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-09” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-09</b></li>
     *   <li>Latitude: <b>-60</b></li>
     *   <li>Longitude: <b>3</b></li>
     *   <li>Easting: <b>678711.584</b></li>
     *   <li>Northing: <b>-12189424.2</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-09 : FORWARD")
    public void GIGS_5101_09() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-60, 3};
        final double[] destinationPoint = new double[]{678711.584, -12189424.2};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-10” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-10</b></li>
     *   <li>Latitude: <b>-80</b></li>
     *   <li>Longitude: <b>3</b></li>
     *   <li>Easting: <b>496813.178</b></li>
     *   <li>Northing: <b>-14413222.698</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-10 : REVERSE")
    public void GIGS_5101_10() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{496813.178, -14413222.698};
        final double[] destinationPoint = new double[]{-80, 3};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-11” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-11</b></li>
     *   <li>Latitude: <b>80</b></li>
     *   <li>Longitude: <b>-2</b></li>
     *   <li>Easting: <b>400000</b></li>
     *   <li>Northing: <b>3354134.429</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-11 : REVERSE")
    public void GIGS_5101_11() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{400000, 3354134.429};
        final double[] destinationPoint = new double[]{80, -2};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-12” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-12</b></li>
     *   <li>Latitude: <b>60</b></li>
     *   <li>Longitude: <b>-2</b></li>
     *   <li>Easting: <b>400000</b></li>
     *   <li>Northing: <b>1123956.966</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-12 : FORWARD")
    public void GIGS_5101_12() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{60, -2};
        final double[] destinationPoint = new double[]{400000, 1123956.966};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-13” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-13</b></li>
     *   <li>Latitude: <b>49</b></li>
     *   <li>Longitude: <b>-2</b></li>
     *   <li>Easting: <b>400000</b></li>
     *   <li>Northing: <b>-100000</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-13 : REVERSE")
    public void GIGS_5101_13() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{400000, -100000};
        final double[] destinationPoint = new double[]{49, -2};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-14” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-14</b></li>
     *   <li>Latitude: <b>40</b></li>
     *   <li>Longitude: <b>-2</b></li>
     *   <li>Easting: <b>400000</b></li>
     *   <li>Northing: <b>-1099699.834</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-14 : FORWARD")
    public void GIGS_5101_14() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{40, -2};
        final double[] destinationPoint = new double[]{400000, -1099699.834};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-15” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-15</b></li>
     *   <li>Latitude: <b>20</b></li>
     *   <li>Longitude: <b>-2</b></li>
     *   <li>Easting: <b>400000</b></li>
     *   <li>Northing: <b>-3315978.565</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-15 : REVERSE")
    public void GIGS_5101_15() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{400000, -3315978.565};
        final double[] destinationPoint = new double[]{20, -2};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-16” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-16</b></li>
     *   <li>Latitude: <b>0</b></li>
     *   <li>Longitude: <b>-2</b></li>
     *   <li>Easting: <b>400000</b></li>
     *   <li>Northing: <b>-5527462.686</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-16 : FORWARD")
    public void GIGS_5101_16() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{0, -2};
        final double[] destinationPoint = new double[]{400000, -5527462.686};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-17” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-17</b></li>
     *   <li>Latitude: <b>-20</b></li>
     *   <li>Longitude: <b>-2</b></li>
     *   <li>Easting: <b>400000</b></li>
     *   <li>Northing: <b>-7738946.807</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-17 : REVERSE")
    public void GIGS_5101_17() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{400000, -7738946.807};
        final double[] destinationPoint = new double[]{-20, -2};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-18” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-18</b></li>
     *   <li>Latitude: <b>-40</b></li>
     *   <li>Longitude: <b>-2</b></li>
     *   <li>Easting: <b>400000</b></li>
     *   <li>Northing: <b>-9955225.538</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-18 : FORWARD")
    public void GIGS_5101_18() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-40, -2};
        final double[] destinationPoint = new double[]{400000, -9955225.538};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-19” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-19</b></li>
     *   <li>Latitude: <b>-60</b></li>
     *   <li>Longitude: <b>-2</b></li>
     *   <li>Easting: <b>400000</b></li>
     *   <li>Northing: <b>-12178882.338</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-19 : REVERSE")
    public void GIGS_5101_19() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{400000, -12178882.338};
        final double[] destinationPoint = new double[]{-60, -2};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-20” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-20</b></li>
     *   <li>Latitude: <b>-80</b></li>
     *   <li>Longitude: <b>-2</b></li>
     *   <li>Easting: <b>400000</b></li>
     *   <li>Northing: <b>-14409059.8</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-20 : FORWARD")
    public void GIGS_5101_20() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-80, -2};
        final double[] destinationPoint = new double[]{400000, -14409059.8};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-21” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-21</b></li>
     *   <li>Latitude: <b>80</b></li>
     *   <li>Longitude: <b>-5</b></li>
     *   <li>Easting: <b>341867.711</b></li>
     *   <li>Northing: <b>3355633.571</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-21 : FORWARD")
    public void GIGS_5101_21() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{80, -5};
        final double[] destinationPoint = new double[]{341867.711, 3355633.571};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-22” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-22</b></li>
     *   <li>Latitude: <b>60</b></li>
     *   <li>Longitude: <b>-5</b></li>
     *   <li>Easting: <b>232704.966</b></li>
     *   <li>Northing: <b>1127751.264</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-22 : REVERSE")
    public void GIGS_5101_22() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{232704.966, 1127751.264};
        final double[] destinationPoint = new double[]{60, -5};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-23” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-23</b></li>
     *   <li>Latitude: <b>49</b></li>
     *   <li>Longitude: <b>-5</b></li>
     *   <li>Easting: <b>180586.02</b></li>
     *   <li>Northing: <b>-95662.911</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-23 : FORWARD")
    public void GIGS_5101_23() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{49, -5};
        final double[] destinationPoint = new double[]{180586.02, -95662.911};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-24” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-24</b></li>
     *   <li>Latitude: <b>40</b></li>
     *   <li>Longitude: <b>-5</b></li>
     *   <li>Easting: <b>143900.026</b></li>
     *   <li>Northing: <b>-1095387.991</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-24 : REVERSE")
    public void GIGS_5101_24() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{143900.026, -1095387.991};
        final double[] destinationPoint = new double[]{40, -5};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-25” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-25</b></li>
     *   <li>Latitude: <b>20</b></li>
     *   <li>Longitude: <b>-5</b></li>
     *   <li>Easting: <b>86073.28</b></li>
     *   <li>Northing: <b>-3313165.843</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-25 : FORWARD")
    public void GIGS_5101_25() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{20, -5};
        final double[] destinationPoint = new double[]{86073.28, -3313165.843};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-26” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-26</b></li>
     *   <li>Latitude: <b>0</b></li>
     *   <li>Longitude: <b>-5</b></li>
     *   <li>Easting: <b>66021.018</b></li>
     *   <li>Northing: <b>-5527462.686</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-26 : REVERSE")
    public void GIGS_5101_26() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{66021.018, -5527462.686};
        final double[] destinationPoint = new double[]{0, -5};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-27” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-27</b></li>
     *   <li>Latitude: <b>-20</b></li>
     *   <li>Longitude: <b>-5</b></li>
     *   <li>Easting: <b>86073.28</b></li>
     *   <li>Northing: <b>-7741759.529</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-27 : FORWARD")
    public void GIGS_5101_27() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-20, -5};
        final double[] destinationPoint = new double[]{86073.28, -7741759.529};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-28” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-28</b></li>
     *   <li>Latitude: <b>-40</b></li>
     *   <li>Longitude: <b>-5</b></li>
     *   <li>Easting: <b>143900.026</b></li>
     *   <li>Northing: <b>-9959537.381</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-28 : REVERSE")
    public void GIGS_5101_28() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{143900.026, -9959537.381};
        final double[] destinationPoint = new double[]{-40, -5};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-29” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-29</b></li>
     *   <li>Latitude: <b>-60</b></li>
     *   <li>Longitude: <b>-5</b></li>
     *   <li>Easting: <b>232704.966</b></li>
     *   <li>Northing: <b>-12182676.64</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-29 : FORWARD")
    public void GIGS_5101_29() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-60, -5};
        final double[] destinationPoint = new double[]{232704.966, -12182676.64};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-30” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-30</b></li>
     *   <li>Latitude: <b>-80</b></li>
     *   <li>Longitude: <b>-5</b></li>
     *   <li>Easting: <b>341867.711</b></li>
     *   <li>Northing: <b>-14410558.943</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-30 : REVERSE")
    public void GIGS_5101_30() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{341867.711, -14410558.943};
        final double[] destinationPoint = new double[]{-80, -5};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-31” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-31</b></li>
     *   <li>Latitude: <b>49.7661327</b></li>
     *   <li>Longitude: <b>-7.5559037</b></li>
     *   <li>Easting: <b>0</b></li>
     *   <li>Northing: <b>0</b></li>
     *   <li>Transect: <b>D</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-31 : REVERSE")
    public void GIGS_5101_31() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{0, 0};
        final double[] destinationPoint = new double[]{49.7661327, -7.5559037};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-32” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-32</b></li>
     *   <li>Latitude: <b>0</b></li>
     *   <li>Longitude: <b>-5</b></li>
     *   <li>Easting: <b>66021.018</b></li>
     *   <li>Northing: <b>-5527462.686</b></li>
     *   <li>Transect: <b>E</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-32 : FORWARD")
    public void GIGS_5101_32() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{0, -5};
        final double[] destinationPoint = new double[]{66021.018, -5527462.686};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-33” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-33</b></li>
     *   <li>Latitude: <b>0</b></li>
     *   <li>Longitude: <b>-4</b></li>
     *   <li>Easting: <b>177404.277</b></li>
     *   <li>Northing: <b>-5527462.686</b></li>
     *   <li>Transect: <b>E</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-33 : REVERSE")
    public void GIGS_5101_33() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{177404.277, -5527462.686};
        final double[] destinationPoint = new double[]{0, -4};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-34” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-34</b></li>
     *   <li>Latitude: <b>0</b></li>
     *   <li>Longitude: <b>-3</b></li>
     *   <li>Easting: <b>288719.208</b></li>
     *   <li>Northing: <b>-5527462.686</b></li>
     *   <li>Transect: <b>E</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-34 : FORWARD")
    public void GIGS_5101_34() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{0, -3};
        final double[] destinationPoint = new double[]{288719.208, -5527462.686};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-35” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-35</b></li>
     *   <li>Latitude: <b>0</b></li>
     *   <li>Longitude: <b>-2</b></li>
     *   <li>Easting: <b>400000</b></li>
     *   <li>Northing: <b>-5527462.686</b></li>
     *   <li>Transect: <b>E</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-35 : REVERSE")
    public void GIGS_5101_35() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{400000, -5527462.686};
        final double[] destinationPoint = new double[]{0, -2};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-36” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-36</b></li>
     *   <li>Latitude: <b>0</b></li>
     *   <li>Longitude: <b>-1</b></li>
     *   <li>Easting: <b>511280.792</b></li>
     *   <li>Northing: <b>-5527462.686</b></li>
     *   <li>Transect: <b>E</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-36 : FORWARD")
    public void GIGS_5101_36() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{0, -1};
        final double[] destinationPoint = new double[]{511280.792, -5527462.686};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-37” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-37</b></li>
     *   <li>Latitude: <b>0</b></li>
     *   <li>Longitude: <b>0</b></li>
     *   <li>Easting: <b>622595.723</b></li>
     *   <li>Northing: <b>-5527462.686</b></li>
     *   <li>Transect: <b>E</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-37 : REVERSE")
    public void GIGS_5101_37() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{622595.723, -5527462.686};
        final double[] destinationPoint = new double[]{0, 0};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-38” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-38</b></li>
     *   <li>Latitude: <b>0</b></li>
     *   <li>Longitude: <b>1</b></li>
     *   <li>Easting: <b>733978.982</b></li>
     *   <li>Northing: <b>-5527462.686</b></li>
     *   <li>Transect: <b>E</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-38 : FORWARD")
    public void GIGS_5101_38() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{0, 1};
        final double[] destinationPoint = new double[]{733978.982, -5527462.686};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-39” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-39</b></li>
     *   <li>Latitude: <b>0</b></li>
     *   <li>Longitude: <b>2</b></li>
     *   <li>Easting: <b>845464.865</b></li>
     *   <li>Northing: <b>-5527462.686</b></li>
     *   <li>Transect: <b>E</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-39 : REVERSE")
    public void GIGS_5101_39() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{845464.865, -5527462.686};
        final double[] destinationPoint = new double[]{0, 2};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-40” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-40</b></li>
     *   <li>Latitude: <b>0</b></li>
     *   <li>Longitude: <b>3</b></li>
     *   <li>Easting: <b>957087.829</b></li>
     *   <li>Northing: <b>-5527462.686</b></li>
     *   <li>Transect: <b>E</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-40 : FORWARD")
    public void GIGS_5101_40() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{0, 3};
        final double[] destinationPoint = new double[]{957087.829, -5527462.686};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-41” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-41</b></li>
     *   <li>Latitude: <b>0</b></li>
     *   <li>Longitude: <b>4</b></li>
     *   <li>Easting: <b>1068882.539</b></li>
     *   <li>Northing: <b>-5527462.686</b></li>
     *   <li>Transect: <b>E</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-41 : REVERSE")
    public void GIGS_5101_41() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{1068882.539, -5527462.686};
        final double[] destinationPoint = new double[]{0, 4};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-42” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-42</b></li>
     *   <li>Latitude: <b>0</b></li>
     *   <li>Longitude: <b>5</b></li>
     *   <li>Easting: <b>1180883.933</b></li>
     *   <li>Northing: <b>-5527462.686</b></li>
     *   <li>Transect: <b>E</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-42 : FORWARD")
    public void GIGS_5101_42() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{0, 5};
        final double[] destinationPoint = new double[]{1180883.933, -5527462.686};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-43” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-43</b></li>
     *   <li>Latitude: <b>0</b></li>
     *   <li>Longitude: <b>6</b></li>
     *   <li>Easting: <b>1293127.266</b></li>
     *   <li>Northing: <b>-5527462.686</b></li>
     *   <li>Transect: <b>E</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-43 : REVERSE")
    public void GIGS_5101_43() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{1293127.266, -5527462.686};
        final double[] destinationPoint = new double[]{0, 6};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-44” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-44</b></li>
     *   <li>Latitude: <b>0</b></li>
     *   <li>Longitude: <b>7</b></li>
     *   <li>Easting: <b>1405648.179</b></li>
     *   <li>Northing: <b>-5527462.686</b></li>
     *   <li>Transect: <b>E</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-44 : FORWARD")
    public void GIGS_5101_44() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{0, 7};
        final double[] destinationPoint = new double[]{1405648.179, -5527462.686};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-45” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-45</b></li>
     *   <li>Latitude: <b>0</b></li>
     *   <li>Longitude: <b>8</b></li>
     *   <li>Easting: <b>1518482.747</b></li>
     *   <li>Northing: <b>-5527462.686</b></li>
     *   <li>Transect: <b>E</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-45 : REVERSE")
    public void GIGS_5101_45() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{1518482.747, -5527462.686};
        final double[] destinationPoint = new double[]{0, 8};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-46” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-46</b></li>
     *   <li>Latitude: <b>60</b></li>
     *   <li>Longitude: <b>-5</b></li>
     *   <li>Easting: <b>232704.966</b></li>
     *   <li>Northing: <b>1127751.264</b></li>
     *   <li>Transect: <b>F</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-46 : FORWARD")
    public void GIGS_5101_46() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{60, -5};
        final double[] destinationPoint = new double[]{232704.966, 1127751.264};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-47” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-47</b></li>
     *   <li>Latitude: <b>60</b></li>
     *   <li>Longitude: <b>-4</b></li>
     *   <li>Easting: <b>288455.816</b></li>
     *   <li>Northing: <b>1125643.213</b></li>
     *   <li>Transect: <b>F</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-47 : REVERSE")
    public void GIGS_5101_47() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{288455.816, 1125643.213};
        final double[] destinationPoint = new double[]{60, -4};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-48” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-48</b></li>
     *   <li>Latitude: <b>60</b></li>
     *   <li>Longitude: <b>-3</b></li>
     *   <li>Easting: <b>344223.662</b></li>
     *   <li>Northing: <b>1124378.512</b></li>
     *   <li>Transect: <b>F</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-48 : FORWARD")
    public void GIGS_5101_48() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{60, -3};
        final double[] destinationPoint = new double[]{344223.662, 1124378.512};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-49” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-49</b></li>
     *   <li>Latitude: <b>60</b></li>
     *   <li>Longitude: <b>-2</b></li>
     *   <li>Easting: <b>400000</b></li>
     *   <li>Northing: <b>1123956.966</b></li>
     *   <li>Transect: <b>F</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-49 : REVERSE")
    public void GIGS_5101_49() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{400000, 1123956.966};
        final double[] destinationPoint = new double[]{60, -2};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-50” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-50</b></li>
     *   <li>Latitude: <b>60</b></li>
     *   <li>Longitude: <b>-1</b></li>
     *   <li>Easting: <b>455776.338</b></li>
     *   <li>Northing: <b>1124378.512</b></li>
     *   <li>Transect: <b>F</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-50 : FORWARD")
    public void GIGS_5101_50() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{60, -1};
        final double[] destinationPoint = new double[]{455776.338, 1124378.512};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-51” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-51</b></li>
     *   <li>Latitude: <b>60</b></li>
     *   <li>Longitude: <b>0</b></li>
     *   <li>Easting: <b>511544.184</b></li>
     *   <li>Northing: <b>1125643.213</b></li>
     *   <li>Transect: <b>F</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-51 : REVERSE")
    public void GIGS_5101_51() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{511544.184, 1125643.213};
        final double[] destinationPoint = new double[]{60, 0};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-52” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-52</b></li>
     *   <li>Latitude: <b>60</b></li>
     *   <li>Longitude: <b>1</b></li>
     *   <li>Easting: <b>567295.034</b></li>
     *   <li>Northing: <b>1127751.264</b></li>
     *   <li>Transect: <b>F</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-52 : FORWARD")
    public void GIGS_5101_52() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{60, 1};
        final double[] destinationPoint = new double[]{567295.034, 1127751.264};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-53” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-53</b></li>
     *   <li>Latitude: <b>60</b></li>
     *   <li>Longitude: <b>2</b></li>
     *   <li>Easting: <b>623020.357</b></li>
     *   <li>Northing: <b>1130702.987</b></li>
     *   <li>Transect: <b>F</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-53 : REVERSE")
    public void GIGS_5101_53() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{623020.357, 1130702.987};
        final double[] destinationPoint = new double[]{60, 2};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-54” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-54</b></li>
     *   <li>Latitude: <b>60</b></li>
     *   <li>Longitude: <b>3</b></li>
     *   <li>Easting: <b>678711.584</b></li>
     *   <li>Northing: <b>1134498.83</b></li>
     *   <li>Transect: <b>F</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-54 : FORWARD")
    public void GIGS_5101_54() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{60, 3};
        final double[] destinationPoint = new double[]{678711.584, 1134498.83};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-55” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-55</b></li>
     *   <li>Latitude: <b>60</b></li>
     *   <li>Longitude: <b>4</b></li>
     *   <li>Easting: <b>734360.093</b></li>
     *   <li>Northing: <b>1139139.367</b></li>
     *   <li>Transect: <b>F</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-55 : REVERSE")
    public void GIGS_5101_55() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{734360.093, 1139139.367};
        final double[] destinationPoint = new double[]{60, 4};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-56” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-56</b></li>
     *   <li>Latitude: <b>60</b></li>
     *   <li>Longitude: <b>5</b></li>
     *   <li>Easting: <b>789957.197</b></li>
     *   <li>Northing: <b>1144625.296</b></li>
     *   <li>Transect: <b>F</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-56 : FORWARD")
    public void GIGS_5101_56() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{60, 5};
        final double[] destinationPoint = new double[]{789957.197, 1144625.296};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-57” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-57</b></li>
     *   <li>Latitude: <b>60</b></li>
     *   <li>Longitude: <b>6</b></li>
     *   <li>Easting: <b>845494.132</b></li>
     *   <li>Northing: <b>1150957.434</b></li>
     *   <li>Transect: <b>F</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-57 : REVERSE")
    public void GIGS_5101_57() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{845494.132, 1150957.434};
        final double[] destinationPoint = new double[]{60, 6};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-58” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-58</b></li>
     *   <li>Latitude: <b>60</b></li>
     *   <li>Longitude: <b>7</b></li>
     *   <li>Easting: <b>900962.042</b></li>
     *   <li>Northing: <b>1158136.713</b></li>
     *   <li>Transect: <b>F</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-58 : FORWARD")
    public void GIGS_5101_58() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{60, 7};
        final double[] destinationPoint = new double[]{900962.042, 1158136.713};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-59” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-59</b></li>
     *   <li>Latitude: <b>60</b></li>
     *   <li>Longitude: <b>8</b></li>
     *   <li>Easting: <b>956351.967</b></li>
     *   <li>Northing: <b>1166164.18</b></li>
     *   <li>Transect: <b>F</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-59 : REVERSE")
    public void GIGS_5101_59() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{956351.967, 1166164.18};
        final double[] destinationPoint = new double[]{60, 8};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }
}
