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
 * Verifies the software’s capabilities to perform conversions for the Hotine Oblique Mercator (variant B) map projection.
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
 *   <td><a href="https://github.com/IOGP-GIGS/GIGSTestDataset/blob/main/GIGSTestDatasetFiles/GIGS%205100%20Conversion%20test%20data/ASCII/GIGS_conv_5105_HOM-B_input_part1.txt">{@code GIGS_conv_5105_HOM-B_input_part1.txt}</a>
 * </tr><tr>
 *   <th>Tested API:</th>
 *   <td>{@link CoordinateOperationFactory#createOperation(CoordinateReferenceSystem, CoordinateReferenceSystem)}.</td>
 * </tr><tr>
 *   <th>Expected result:</th>
 *   <td>Results for the forward and reverse calculations should agree to within 0.05m (50mm) or 0.0000006°
 *   of the Test Data. See file GIGS_conv_5105_HOM-B_output_part1.
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
 * public class MyTest extends Test51051 {
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
@DisplayName("Hotine Oblique Mercator (variant B)")
public class Test51051 extends Series5100 {

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
    public Test51051(final Factories factories) throws FactoryException {
        super(factories, 0.05, 0.0000006, 0.006, 0.00000006);
    }

    /**
     * Method used in class instantiation to initialise the geographic CRS and the projected CRS
     * used in this test class.
     * <p>Geographic CRS : GIGS CRS Code 64010; GIGS geogCRS G; GDM2000; decimal degree; EPSG CRS code 4742
     * <p>ProjectedCRS : GIGS CRS Code 62020; GIGS projCRS G13; GDM2000 / East Malaysia BRSO; metre; No direct EPSG equivalent
     *
     * @throws FactoryException if an error occurred while creating a CRS.
     */
    @Override
    protected void createCRSs() throws FactoryException {
        createGeogCRS(Test3205::GIGS_64010);
        createProjCRS(Test3207::GIGS_62020);
    }

    /**
     * Tests “GIGS-5105-01” for HOM-B calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5105-01</b></li>
     *   <li>Latitude: <b>12</b></li>
     *   <li>Longitude: <b>117</b></li>
     *   <li>Easting: <b>807919.144</b></li>
     *   <li>Northing: <b>1329535.334</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: Round Trip calculation point.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5105-01 : FORWARD")
    public void GIGS_5105_01() throws TransformException {
        isRTC = true;
        isForward = true;
        final double[] originPoint = new double[]{12, 117};
        final double[] destinationPoint = new double[]{807919.144, 1329535.334};
        convertAndVerifyRoundTripPoint(originPoint, destinationPoint);
    }

    /**
     * Tests “GIGS-5105-02” for HOM-B calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5105-02</b></li>
     *   <li>Latitude: <b>10</b></li>
     *   <li>Longitude: <b>117</b></li>
     *   <li>Easting: <b>808784.981</b></li>
     *   <li>Northing: <b>1107678.473</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5105-02 : REVERSE")
    public void GIGS_5105_02() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{808784.981, 1107678.473};
        final double[] destinationPoint = new double[]{10, 117};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5105-03” for HOM-B calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5105-03</b></li>
     *   <li>Latitude: <b>9</b></li>
     *   <li>Longitude: <b>117</b></li>
     *   <li>Easting: <b>809334.177</b></li>
     *   <li>Northing: <b>996918.212</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5105-03 : FORWARD")
    public void GIGS_5105_03() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{9, 117};
        final double[] destinationPoint = new double[]{809334.177, 996918.212};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5105-04” for HOM-B calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5105-04</b></li>
     *   <li>Latitude: <b>8</b></li>
     *   <li>Longitude: <b>117</b></li>
     *   <li>Easting: <b>809939.302</b></li>
     *   <li>Northing: <b>886240.183</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5105-04 : REVERSE")
    public void GIGS_5105_04() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{809939.302, 886240.183};
        final double[] destinationPoint = new double[]{8, 117};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5105-05” for HOM-B calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5105-05</b></li>
     *   <li>Latitude: <b>6.87845833</b></li>
     *   <li>Longitude: <b>116.8465522</b></li>
     *   <li>Easting: <b>793704.631</b></li>
     *   <li>Northing: <b>762081.047</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5105-05 : FORWARD")
    public void GIGS_5105_05() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{6.87845833, 116.8465522};
        final double[] destinationPoint = new double[]{793704.631, 762081.047};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5105-06” for HOM-B calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5105-06</b></li>
     *   <li>Latitude: <b>6</b></li>
     *   <li>Longitude: <b>117</b></li>
     *   <li>Easting: <b>811253.303</b></li>
     *   <li>Northing: <b>665041.265</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5105-06 : REVERSE")
    public void GIGS_5105_06() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{811253.303, 665041.265};
        final double[] destinationPoint = new double[]{6, 117};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5105-07” for HOM-B calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5105-07</b></li>
     *   <li>Latitude: <b>5</b></li>
     *   <li>Longitude: <b>117</b></li>
     *   <li>Easting: <b>811930.345</b></li>
     *   <li>Northing: <b>554475.627</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5105-07 : FORWARD")
    public void GIGS_5105_07() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{5, 117};
        final double[] destinationPoint = new double[]{811930.345, 554475.627};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5105-08” for HOM-B calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5105-08</b></li>
     *   <li>Latitude: <b>4</b></li>
     *   <li>Longitude: <b>117</b></li>
     *   <li>Easting: <b>812599.582</b></li>
     *   <li>Northing: <b>443902.706</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5105-08 : REVERSE")
    public void GIGS_5105_08() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{812599.582, 443902.706};
        final double[] destinationPoint = new double[]{4, 117};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5105-09” for HOM-B calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5105-09</b></li>
     *   <li>Latitude: <b>4</b></li>
     *   <li>Longitude: <b>115</b></li>
     *   <li>Easting: <b>590521.147</b></li>
     *   <li>Northing: <b>442890.861</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5105-09 : REVERSE")
    public void GIGS_5105_09() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{590521.147, 442890.861};
        final double[] destinationPoint = new double[]{4, 115};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5105-10” for HOM-B calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5105-10</b></li>
     *   <li>Latitude: <b>3</b></li>
     *   <li>Longitude: <b>117</b></li>
     *   <li>Easting: <b>813245.133</b></li>
     *   <li>Northing: <b>333300.13</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5105-10 : FORWARD")
    public void GIGS_5105_10() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{3, 117};
        final double[] destinationPoint = new double[]{813245.133, 333300.13};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5105-11” for HOM-B calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5105-11</b></li>
     *   <li>Latitude: <b>2</b></li>
     *   <li>Longitude: <b>117</b></li>
     *   <li>Easting: <b>813851.067</b></li>
     *   <li>Northing: <b>222645.511</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5105-11 : REVERSE")
    public void GIGS_5105_11() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{813851.067, 222645.511};
        final double[] destinationPoint = new double[]{2, 117};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5105-12” for HOM-B calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5105-12</b></li>
     *   <li>Latitude: <b>1</b></li>
     *   <li>Longitude: <b>117</b></li>
     *   <li>Easting: <b>814401.375</b></li>
     *   <li>Northing: <b>111916.452</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5105-12 : FORWARD")
    public void GIGS_5105_12() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{1, 117};
        final double[] destinationPoint = new double[]{814401.375, 111916.452};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5105-13” for HOM-B calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5105-13</b></li>
     *   <li>Latitude: <b>-0.00017333</b></li>
     *   <li>Longitude: <b>109.6858208</b></li>
     *   <li>Easting: <b>0</b></li>
     *   <li>Northing: <b>0</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5105-13 : REVERSE")
    public void GIGS_5105_13() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{0, 0};
        final double[] destinationPoint = new double[]{-0.00017333, 109.6858208};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5105-14” for HOM-B calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5105-14</b></li>
     *   <li>Latitude: <b>6</b></li>
     *   <li>Longitude: <b>123</b></li>
     *   <li>Easting: <b>1475669.281</b></li>
     *   <li>Northing: <b>673118.573</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5105-14 : FORWARD")
    public void GIGS_5105_14() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{6, 123};
        final double[] destinationPoint = new double[]{1475669.281, 673118.573};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5105-15” for HOM-B calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5105-15</b></li>
     *   <li>Latitude: <b>6</b></li>
     *   <li>Longitude: <b>122</b></li>
     *   <li>Easting: <b>1364854.862</b></li>
     *   <li>Northing: <b>671146.254</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5105-15 : REVERSE")
    public void GIGS_5105_15() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{1364854.862, 671146.254};
        final double[] destinationPoint = new double[]{6, 122};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5105-16” for HOM-B calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5105-16</b></li>
     *   <li>Latitude: <b>6</b></li>
     *   <li>Longitude: <b>121</b></li>
     *   <li>Easting: <b>1254086.173</b></li>
     *   <li>Northing: <b>669446.249</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5105-16 : FORWARD")
    public void GIGS_5105_16() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{6, 121};
        final double[] destinationPoint = new double[]{1254086.173, 669446.249};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5105-17” for HOM-B calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5105-17</b></li>
     *   <li>Latitude: <b>6</b></li>
     *   <li>Longitude: <b>120</b></li>
     *   <li>Easting: <b>1143352.598</b></li>
     *   <li>Northing: <b>668002.074</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5105-17 : REVERSE")
    public void GIGS_5105_17() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{1143352.598, 668002.074};
        final double[] destinationPoint = new double[]{6, 120};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5105-18” for HOM-B calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5105-18</b></li>
     *   <li>Latitude: <b>6</b></li>
     *   <li>Longitude: <b>119</b></li>
     *   <li>Easting: <b>1032643.312</b></li>
     *   <li>Northing: <b>666797.354</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5105-18 : FORWARD")
    public void GIGS_5105_18() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{6, 119};
        final double[] destinationPoint = new double[]{1032643.312, 666797.354};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5105-19” for HOM-B calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5105-19</b></li>
     *   <li>Latitude: <b>6</b></li>
     *   <li>Longitude: <b>118</b></li>
     *   <li>Easting: <b>921947.286</b></li>
     *   <li>Northing: <b>665815.815</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5105-19 : REVERSE")
    public void GIGS_5105_19() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{921947.286, 665815.815};
        final double[] destinationPoint = new double[]{6, 118};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5105-20” for HOM-B calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5105-20</b></li>
     *   <li>Latitude: <b>6</b></li>
     *   <li>Longitude: <b>117</b></li>
     *   <li>Easting: <b>811253.303</b></li>
     *   <li>Northing: <b>665041.265</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5105-20 : FORWARD")
    public void GIGS_5105_20() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{6, 117};
        final double[] destinationPoint = new double[]{811253.303, 665041.265};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5105-21” for HOM-B calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5105-21</b></li>
     *   <li>Latitude: <b>6</b></li>
     *   <li>Longitude: <b>116</b></li>
     *   <li>Easting: <b>700549.965</b></li>
     *   <li>Northing: <b>664457.586</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5105-21 : REVERSE")
    public void GIGS_5105_21() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{700549.965, 664457.586};
        final double[] destinationPoint = new double[]{6, 116};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5105-22” for HOM-B calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5105-22</b></li>
     *   <li>Latitude: <b>6</b></li>
     *   <li>Longitude: <b>115</b></li>
     *   <li>Easting: <b>589825.706</b></li>
     *   <li>Northing: <b>664048.715</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5105-22 : FORWARD")
    public void GIGS_5105_22() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{6, 115};
        final double[] destinationPoint = new double[]{589825.706, 664048.715};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5105-23” for HOM-B calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5105-23</b></li>
     *   <li>Latitude: <b>6</b></li>
     *   <li>Longitude: <b>114</b></li>
     *   <li>Easting: <b>479068.802</b></li>
     *   <li>Northing: <b>663798.63</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5105-23 : REVERSE")
    public void GIGS_5105_23() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{479068.802, 663798.63};
        final double[] destinationPoint = new double[]{6, 114};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }
}
