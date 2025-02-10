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
 * Verifies the software’s capabilities to perform conversions for the Lambert Conic Conformal (2SP) map projection.
 *
 * <table class="gigs">
 * <caption>Test description</caption>
 * <tr>
 *   <th>Test method:</th>
 *   <td><ul>
 *      <li>Invoke coordinate conversions in both directions and inspect results</li>
 *      <li>For the first test point in part 1, perform iterations of forward and reverse computations using the
 *      output of computation n as input into computation n+1, until the output coordinate values exceed
 *      more than 0.02 ftUS/ft (0.24in) or 0.00000006° from the original calculated values (but no more than
 *      1000 iterations).</li></ul>
 *   </td>
 * </tr><tr>
 *   <th>Test data folder:</th>
 *   <td><a href="https://github.com/IOGP-GIGS/GIGSTestDataset/blob/main/GIGSTestDatasetFiles/GIGS%205100%20Conversion%20test%20data/ASCII/GIGS_conv_5103_LCC2_input_part1.txt">{@code GIGS_conv_5103_LCC2_input_part1.txt}</a>
 * </tr><tr>
 *   <th>Tested API:</th>
 *   <td>{@link CoordinateOperationFactory#createOperation(CoordinateReferenceSystem, CoordinateReferenceSystem)}.</td>
 * </tr><tr>
 *   <th>Expected result:</th>
 *   <td>Results for the forward and reverse calculations should agree to within 0.1 ftUS/ft (1.2in) or 0.0000003°
 *   of the Test Data. See file GIGS_conv_5103_LCC2_output_part1.
 *   For the round trip calculation the initial calculated coordinates of the point should change by less
 *   than 0.02 ftUS/ft (or 0.24in) 0.00000006° (before 1000 iterations).
 *   Test result will be pass or fail. If fail, details of failure should be reported.</td>
 * </tr></table>
 *
 *
 * <h2>Usage example</h2>
 * In order to specify their factories and run the tests in a JUnit framework, implementers can
 * define a subclass in their own test suite as in the example below:
 * <p>
 * {@snippet lang = "java":
 * public class MyTest extends Test51031 {
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
@DisplayName("Lambert Conic Conformal (2SP)")
public class Test51031 extends Series5100 {

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
    public Test51031(final Factories factories) throws FactoryException {
        super(factories, 0.03, 0.0000003, 0.006, 0.00000006);
    }

    /**
     * Method used in class instantiation to initialise the geographic CRS and the projected CRS
     * used in this test class.
     * <p>Geographic CRS : GIGS CRS Code 64008; GIGS geogCRS E; Belge 1972; decimal degree; EPSG CRS code 4313
     * <p>ProjectedCRS : GIGS CRS Code 62013; GIGS projCRS E6; Belge 1972 / Belgian Lambert 1972; metre; EPSG CRS code 31370
     *
     * @throws FactoryException if an error occurred while creating a CRS.
     */
    @Override
    protected void createCRSs() throws FactoryException {
        createGeogCRS(Test3205::GIGS_64008);
        createProjCRS(Test3207::GIGS_62013);
    }

    /**
     * Tests “GIGS-5103-01” for LCC2 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5103-01</b></li>
     *   <li>Latitude: <b>58</b></li>
     *   <li>Longitude: <b>5</b></li>
     *   <li>Easting: <b>187742.7</b></li>
     *   <li>Northing: <b>969521.653</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: Round Trip calculation point.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5103-01 : FORWARD")
    public void GIGS_5103_01() throws TransformException {
        isRTC = true;
        isForward = true;
        final double[] originPoint = new double[]{58, 5};
        final double[] destinationPoint = new double[]{187742.7, 969521.653};
        convertAndVerifyRoundTripPoint(originPoint, destinationPoint);
    }

    /**
     * Tests “GIGS-5103-02” for LCC2 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5103-02</b></li>
     *   <li>Latitude: <b>57</b></li>
     *   <li>Longitude: <b>5</b></li>
     *   <li>Easting: <b>188698.877</b></li>
     *   <li>Northing: <b>857277.135</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5103-02 : REVERSE")
    public void GIGS_5103_02() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{188698.877, 857277.135};
        final double[] destinationPoint = new double[]{57, 5};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5103-03” for LCC2 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5103-03</b></li>
     *   <li>Latitude: <b>56</b></li>
     *   <li>Longitude: <b>5</b></li>
     *   <li>Easting: <b>189652.853</b></li>
     *   <li>Northing: <b>745291.184</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5103-03 : FORWARD")
    public void GIGS_5103_03() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{56, 5};
        final double[] destinationPoint = new double[]{189652.853, 745291.184};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5103-04” for LCC2 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5103-04</b></li>
     *   <li>Latitude: <b>55</b></li>
     *   <li>Longitude: <b>5</b></li>
     *   <li>Easting: <b>190604.967</b></li>
     *   <li>Northing: <b>633523.672</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5103-04 : REVERSE")
    public void GIGS_5103_04() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{190604.967, 633523.672};
        final double[] destinationPoint = new double[]{55, 5};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5103-05” for LCC2 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5103-05</b></li>
     *   <li>Latitude: <b>54</b></li>
     *   <li>Longitude: <b>5</b></li>
     *   <li>Easting: <b>191555.55</b></li>
     *   <li>Northing: <b>521935.9</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5103-05 : FORWARD")
    public void GIGS_5103_05() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{54, 5};
        final double[] destinationPoint = new double[]{191555.55, 521935.9};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5103-06” for LCC2 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5103-06</b></li>
     *   <li>Latitude: <b>53</b></li>
     *   <li>Longitude: <b>5</b></li>
     *   <li>Easting: <b>192504.921</b></li>
     *   <li>Northing: <b>410490.433</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5103-06 : REVERSE")
    public void GIGS_5103_06() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{192504.921, 410490.433};
        final double[] destinationPoint = new double[]{53, 5};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5103-07” for LCC2 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5103-07</b></li>
     *   <li>Latitude: <b>52.15616056</b></li>
     *   <li>Longitude: <b>5.387638889</b></li>
     *   <li>Easting: <b>219843.841</b></li>
     *   <li>Northing: <b>316827.604</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5103-07 : FORWARD")
    public void GIGS_5103_07() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{52.15616056, 5.387638889};
        final double[] destinationPoint = new double[]{219843.841, 316827.604};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5103-08” for LCC2 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5103-08</b></li>
     *   <li>Latitude: <b>51</b></li>
     *   <li>Longitude: <b>4</b></li>
     *   <li>Easting: <b>124202.936</b></li>
     *   <li>Northing: <b>187756.876</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5103-08 : REVERSE")
    public void GIGS_5103_08() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{124202.936, 187756.876};
        final double[] destinationPoint = new double[]{51, 4};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5103-09” for LCC2 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5103-09</b></li>
     *   <li>Latitude: <b>50</b></li>
     *   <li>Longitude: <b>4</b></li>
     *   <li>Easting: <b>123652.406</b></li>
     *   <li>Northing: <b>76521.628</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5103-09 : FORWARD")
    public void GIGS_5103_09() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{50, 4};
        final double[] destinationPoint = new double[]{123652.406, 76521.628};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5103-10” for LCC2 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5103-10</b></li>
     *   <li>Latitude: <b>49</b></li>
     *   <li>Longitude: <b>4</b></li>
     *   <li>Easting: <b>123101.889</b></li>
     *   <li>Northing: <b>-34711.068</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5103-10 : REVERSE")
    public void GIGS_5103_10() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{123101.889, -34711.068};
        final double[] destinationPoint = new double[]{49, 4};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5103-11” for LCC2 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5103-11</b></li>
     *   <li>Latitude: <b>47.97526111</b></li>
     *   <li>Longitude: <b>3.31372806</b></li>
     *   <li>Easting: <b>71254.553</b></li>
     *   <li>Northing: <b>-148236.592</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5103-11 : FORWARD")
    public void GIGS_5103_11() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{47.97526111, 3.31372806};
        final double[] destinationPoint = new double[]{71254.553, -148236.592};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5103-12” for LCC2 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5103-12</b></li>
     *   <li>Latitude: <b>53</b></li>
     *   <li>Longitude: <b>3</b></li>
     *   <li>Easting: <b>58108.966</b></li>
     *   <li>Northing: <b>411155.591</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5103-12 : FORWARD")
    public void GIGS_5103_12() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{53, 3};
        final double[] destinationPoint = new double[]{58108.966, 411155.591};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5103-13” for LCC2 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5103-13</b></li>
     *   <li>Latitude: <b>53</b></li>
     *   <li>Longitude: <b>4</b></li>
     *   <li>Easting: <b>125304.704</b></li>
     *   <li>Northing: <b>410370.504</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5103-13 : REVERSE")
    public void GIGS_5103_13() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{125304.704, 410370.504};
        final double[] destinationPoint = new double[]{53, 4};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5103-14” for LCC2 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5103-14</b></li>
     *   <li>Latitude: <b>53</b></li>
     *   <li>Longitude: <b>5</b></li>
     *   <li>Easting: <b>192504.921</b></li>
     *   <li>Northing: <b>410490.433</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5103-14 : FORWARD")
    public void GIGS_5103_14() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{53, 5};
        final double[] destinationPoint = new double[]{192504.921, 410490.433};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5103-15” for LCC2 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5103-15</b></li>
     *   <li>Latitude: <b>53</b></li>
     *   <li>Longitude: <b>6</b></li>
     *   <li>Easting: <b>259697.429</b></li>
     *   <li>Northing: <b>411515.356</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5103-15 : REVERSE")
    public void GIGS_5103_15() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{259697.429, 411515.356};
        final double[] destinationPoint = new double[]{53, 6};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5103-16” for LCC2 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5103-16</b></li>
     *   <li>Latitude: <b>53</b></li>
     *   <li>Longitude: <b>7</b></li>
     *   <li>Easting: <b>326870.04</b></li>
     *   <li>Northing: <b>413445.087</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5103-16 : FORWARD")
    public void GIGS_5103_16() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{53, 7};
        final double[] destinationPoint = new double[]{326870.04, 413445.087};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5103-17” for LCC2 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5103-17</b></li>
     *   <li>Latitude: <b>53</b></li>
     *   <li>Longitude: <b>8</b></li>
     *   <li>Easting: <b>394010.571</b></li>
     *   <li>Northing: <b>416279.276</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5103-17 : REVERSE")
    public void GIGS_5103_17() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{394010.571, 416279.276};
        final double[] destinationPoint = new double[]{53, 8};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5103-18” for LCC2 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5103-18</b></li>
     *   <li>Latitude: <b>53</b></li>
     *   <li>Longitude: <b>9</b></li>
     *   <li>Easting: <b>461106.844</b></li>
     *   <li>Northing: <b>420017.408</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5103-18 : FORWARD")
    public void GIGS_5103_18() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{53, 9};
        final double[] destinationPoint = new double[]{461106.844, 420017.408};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5103-19” for LCC2 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5103-19</b></li>
     *   <li>Latitude: <b>53</b></li>
     *   <li>Longitude: <b>10</b></li>
     *   <li>Easting: <b>528146.69</b></li>
     *   <li>Northing: <b>424658.807</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5103-19 : REVERSE")
    public void GIGS_5103_19() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{528146.69, 424658.807};
        final double[] destinationPoint = new double[]{53, 10};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5103-20” for LCC2 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5103-20</b></li>
     *   <li>Latitude: <b>53</b></li>
     *   <li>Longitude: <b>11</b></li>
     *   <li>Easting: <b>595117.95</b></li>
     *   <li>Northing: <b>430202.63</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5103-20 : FORWARD")
    public void GIGS_5103_20() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{53, 11};
        final double[] destinationPoint = new double[]{595117.95, 430202.63};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }
}
