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
 * Verifies the software’s capabilities to perform conversions for the Cassini-Soldner map projection.
 *
 * <table class="gigs">
 * <caption>Test description</caption>
 * <tr>
 *   <th>Test method:</th>
 *   <td><ul>
 *      <li>Invoke coordinate conversions in both directions and inspect results</li>
 *      <li>For the last test point, perform iterations of forward and reverse computations using the
 *      output of computation n as input into computation n+1, until the output coordinate values exceed
 *      more than 0.006m (6mm) or 0.00000006° from the original calculated values (but no more than
 *      1000 iterations).</li></ul>
 *   </td>
 * </tr><tr>
 *   <th>Test data folder:</th>
 *   <td><a href="https://github.com/IOGP-GIGS/GIGSTestDataset/blob/main/GIGSTestDatasetFiles/GIGS%205100%20Conversion%20test%20data/ASCII/GIGS_conv_5108_Cass_input.txt">{@code GIGS_conv_5108_Cass_input.txt}</a>
 * </tr><tr>
 *   <th>Tested API:</th>
 *   <td>{@link CoordinateOperationFactory#createOperation(CoordinateReferenceSystem, CoordinateReferenceSystem)}.</td>
 * </tr><tr>
 *   <th>Expected result:</th>
 *   <td>Results for the forward and reverse calculations should agree to within 0.05m (50mm) or 0.0000006°
 *   of the Test Data. See file GIGS_conv_5108_Cass_output.
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
 * public class MyTest extends Test5108 {
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
@DisplayName("Cassini-Soldner")
public class Test5108 extends Series5100 {

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
    public Test5108(final Factories factories) throws FactoryException {
        super(factories, 0.05, 0.0000006, 0.006, 0.00000006);
    }

    /**
     * Method used in class instantiation to initialise the geographic CRS and the projected CRS
     * used in this test class.
     * <p>Geographic CRS : GIGS CRS Code 64010; GIGS geogCRS G; GDM2000; decimal degree; EPSG CRS code 4742
     * <p>ProjectedCRS : GIGS CRS Code 62022; GIGS projCRS G15; GDM2000 / Johor Grid; metre; EPSG CRS code 3377
     *
     * @throws FactoryException if an error occurred while creating a CRS.
     */
    @Override
    protected void createCRSs() throws FactoryException {
        createGeogCRS(Test3205::GIGS_64010);
        createProjCRS(Test3207::GIGS_62022);
    }

    /**
     * Tests “GIGS-5108-01” for Cass calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5108-01</b></li>
     *   <li>Latitude: <b>10</b></li>
     *   <li>Longitude: <b>106</b></li>
     *   <li>Easting: <b>267186.017</b></li>
     *   <li>Northing: <b>881108.902</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5108-01 : REVERSE")
    public void GIGS_5108_01() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{267186.017, 881108.902};
        final double[] destinationPoint = new double[]{10, 106};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5108-02” for Cass calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5108-02</b></li>
     *   <li>Latitude: <b>9</b></li>
     *   <li>Longitude: <b>106</b></li>
     *   <li>Easting: <b>268006.024</b></li>
     *   <li>Northing: <b>770398.186</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5108-02 : FORWARD")
    public void GIGS_5108_02() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{9, 106};
        final double[] destinationPoint = new double[]{268006.024, 770398.186};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5108-03” for Cass calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5108-03</b></li>
     *   <li>Latitude: <b>8</b></li>
     *   <li>Longitude: <b>106</b></li>
     *   <li>Easting: <b>268740.351</b></li>
     *   <li>Northing: <b>659692.254</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5108-03 : REVERSE")
    public void GIGS_5108_03() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{268740.351, 659692.254};
        final double[] destinationPoint = new double[]{8, 106};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5108-04” for Cass calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5108-04</b></li>
     *   <li>Latitude: <b>7</b></li>
     *   <li>Longitude: <b>106</b></li>
     *   <li>Easting: <b>269388.786</b></li>
     *   <li>Northing: <b>548990.588</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5108-04 : FORWARD")
    public void GIGS_5108_04() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{7, 106};
        final double[] destinationPoint = new double[]{269388.786, 548990.588};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5108-05” for Cass calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5108-05</b></li>
     *   <li>Latitude: <b>6</b></li>
     *   <li>Longitude: <b>106</b></li>
     *   <li>Easting: <b>269951.141</b></li>
     *   <li>Northing: <b>438292.666</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5108-05 : REVERSE")
    public void GIGS_5108_05() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{269951.141, 438292.666};
        final double[] destinationPoint = new double[]{6, 106};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5108-06” for Cass calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5108-06</b></li>
     *   <li>Latitude: <b>5</b></li>
     *   <li>Longitude: <b>106</b></li>
     *   <li>Easting: <b>270427.255</b></li>
     *   <li>Northing: <b>327597.962</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5108-06 : FORWARD")
    public void GIGS_5108_06() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{5, 106};
        final double[] destinationPoint = new double[]{270427.255, 327597.962};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5108-07” for Cass calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5108-07</b></li>
     *   <li>Latitude: <b>4</b></li>
     *   <li>Longitude: <b>106</b></li>
     *   <li>Easting: <b>270816.99</b></li>
     *   <li>Northing: <b>216905.945</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5108-07 : REVERSE")
    public void GIGS_5108_07() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{270816.99, 216905.945};
        final double[] destinationPoint = new double[]{4, 106};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5108-08” for Cass calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5108-08</b></li>
     *   <li>Latitude: <b>3</b></li>
     *   <li>Longitude: <b>106</b></li>
     *   <li>Easting: <b>271120.234</b></li>
     *   <li>Northing: <b>106216.081</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5108-08 : FORWARD")
    public void GIGS_5108_08() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{3, 106};
        final double[] destinationPoint = new double[]{271120.234, 106216.081};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5108-09” for Cass calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5108-09</b></li>
     *   <li>Latitude: <b>2.04246768</b></li>
     *   <li>Longitude: <b>103.5610658</b></li>
     *   <li>Easting: <b>0</b></li>
     *   <li>Northing: <b>0</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5108-09 : REVERSE")
    public void GIGS_5108_09() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{0, 0};
        final double[] destinationPoint = new double[]{2.04246768, 103.5610658};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5108-10” for Cass calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5108-10</b></li>
     *   <li>Latitude: <b>1.82776484</b></li>
     *   <li>Longitude: <b>103.6402598</b></li>
     *   <li>Easting: <b>8813.252</b></li>
     *   <li>Northing: <b>-23740.095</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5108-10 : REVERSE")
    public void GIGS_5108_10() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{8813.252, -23740.095};
        final double[] destinationPoint = new double[]{1.82776484, 103.6402598};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5108-11” for Cass calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5108-11</b></li>
     *   <li>Latitude: <b>1</b></li>
     *   <li>Longitude: <b>106</b></li>
     *   <li>Easting: <b>271466.923</b></li>
     *   <li>Northing: <b>-115159.332</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5108-11 : FORWARD")
    public void GIGS_5108_11() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{1, 106};
        final double[] destinationPoint = new double[]{271466.923, -115159.332};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5108-12” for Cass calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5108-12</b></li>
     *   <li>Latitude: <b>5</b></li>
     *   <li>Longitude: <b>109</b></li>
     *   <li>Easting: <b>603116.703</b></li>
     *   <li>Northing: <b>329668.599</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5108-12 : FORWARD")
    public void GIGS_5108_12() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{5, 109};
        final double[] destinationPoint = new double[]{603116.703, 329668.599};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5108-13” for Cass calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5108-13</b></li>
     *   <li>Latitude: <b>5</b></li>
     *   <li>Longitude: <b>108</b></li>
     *   <li>Easting: <b>492221.308</b></li>
     *   <li>Northing: <b>328807.336</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5108-13 : REVERSE")
    public void GIGS_5108_13() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{492221.308, 328807.336};
        final double[] destinationPoint = new double[]{5, 108};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5108-14” for Cass calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5108-14</b></li>
     *   <li>Latitude: <b>5</b></li>
     *   <li>Longitude: <b>107</b></li>
     *   <li>Easting: <b>381324.74</b></li>
     *   <li>Northing: <b>328117.472</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5108-14 : FORWARD")
    public void GIGS_5108_14() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{5, 107};
        final double[] destinationPoint = new double[]{381324.74, 328117.472};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5108-15” for Cass calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5108-15</b></li>
     *   <li>Latitude: <b>5</b></li>
     *   <li>Longitude: <b>106</b></li>
     *   <li>Easting: <b>270427.255</b></li>
     *   <li>Northing: <b>327597.962</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5108-15 : REVERSE")
    public void GIGS_5108_15() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{270427.255, 327597.962};
        final double[] destinationPoint = new double[]{5, 106};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5108-16” for Cass calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5108-16</b></li>
     *   <li>Latitude: <b>5</b></li>
     *   <li>Longitude: <b>105</b></li>
     *   <li>Easting: <b>159529.111</b></li>
     *   <li>Northing: <b>327248.012</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5108-16 : FORWARD")
    public void GIGS_5108_16() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{5, 105};
        final double[] destinationPoint = new double[]{159529.111, 327248.012};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5108-17” for Cass calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5108-17</b></li>
     *   <li>Latitude: <b>5</b></li>
     *   <li>Longitude: <b>104</b></li>
     *   <li>Easting: <b>48630.563</b></li>
     *   <li>Northing: <b>327067.097</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: Round Trip calculation point.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5108-17 : REVERSE")
    public void GIGS_5108_17() throws TransformException {
        isRTC = true;
        isForward = false;
        final double[] originPoint = new double[]{48630.563, 327067.097};
        final double[] destinationPoint = new double[]{5, 104};
        convertAndVerifyRoundTripPoint(originPoint, destinationPoint);
    }
}
