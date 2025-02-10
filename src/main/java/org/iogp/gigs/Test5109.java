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
 * Verifies the software’s capabilities to perform conversions for the Albers Equal Area map projection.
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
 *   <td><a href="https://github.com/IOGP-GIGS/GIGSTestDataset/blob/main/GIGSTestDatasetFiles/GIGS%205100%20Conversion%20test%20data/ASCII/GIGS_conv_5109_Albers_input.txt">{@code GIGS_conv_5109_Albers_input.txt}</a>
 * </tr><tr>
 *   <th>Tested API:</th>
 *   <td>{@link CoordinateOperationFactory#createOperation(CoordinateReferenceSystem, CoordinateReferenceSystem)}.</td>
 * </tr><tr>
 *   <th>Expected result:</th>
 *   <td>Results for the forward and reverse calculations should agree to within 0.05m (50mm) or 0.0000006°
 *   of the Test Data. See file GIGS_conv_5109_Albers_output.
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
 * public class MyTest extends Test5109 {
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
@DisplayName("Albers Equal Area")
public class Test5109 extends Series5100 {

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
    public Test5109(final Factories factories) throws FactoryException {
        super(factories, 0.05, 0.0000006, 0.006, 0.00000006);
    }

    /**
     * Method used in class instantiation to initialise the geographic CRS and the projected CRS
     * used in this test class.
     * <p>Geographic CRS : GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283
     * <p>ProjectedCRS : GIGS CRS Code 62016; GIGS projCRS F9; GDA94 / Australian Albers; metre; EPSG CRS code 3577
     *
     * @throws FactoryException if an error occurred while creating a CRS.
     */
    @Override
    protected void createCRSs() throws FactoryException {
        createGeogCRS(Test3205::GIGS_64009);
        createProjCRS(Test3207::GIGS_62016);
    }

    /**
     * Tests “GIGS-5109-01” for Albers calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5109-01</b></li>
     *   <li>Latitude: <b>0</b></li>
     *   <li>Longitude: <b>132</b></li>
     *   <li>Easting: <b>0</b></li>
     *   <li>Northing: <b>0</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5109-01 : REVERSE")
    public void GIGS_5109_01() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{0, 0};
        final double[] destinationPoint = new double[]{0, 132};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5109-02” for Albers calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5109-02</b></li>
     *   <li>Latitude: <b>-27</b></li>
     *   <li>Longitude: <b>132</b></li>
     *   <li>Easting: <b>0</b></li>
     *   <li>Northing: <b>-2926820.89</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5109-02 : FORWARD")
    public void GIGS_5109_02() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-27, 132};
        final double[] destinationPoint = new double[]{0, -2926820.89};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5109-03” for Albers calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5109-03</b></li>
     *   <li>Latitude: <b>0</b></li>
     *   <li>Longitude: <b>140</b></li>
     *   <li>Easting: <b>966973.979</b></li>
     *   <li>Northing: <b>-30285.601</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5109-03 : REVERSE")
    public void GIGS_5109_03() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{966973.979, -30285.601};
        final double[] destinationPoint = new double[]{0, 140};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5109-04” for Albers calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5109-04</b></li>
     *   <li>Latitude: <b>-20</b></li>
     *   <li>Longitude: <b>140</b></li>
     *   <li>Easting: <b>832799.36</b></li>
     *   <li>Northing: <b>-2170181.926</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5109-04 : FORWARD")
    public void GIGS_5109_04() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-20, 140};
        final double[] destinationPoint = new double[]{832799.36, -2170181.926};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5109-05” for Albers calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5109-05</b></li>
     *   <li>Latitude: <b>-40</b></li>
     *   <li>Longitude: <b>140</b></li>
     *   <li>Easting: <b>693250.209</b></li>
     *   <li>Northing: <b>-4395794.489</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5109-05 : REVERSE")
    public void GIGS_5109_05() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{693250.209, -4395794.489};
        final double[] destinationPoint = new double[]{-40, 140};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5109-06” for Albers calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5109-06</b></li>
     *   <li>Latitude: <b>-60</b></li>
     *   <li>Longitude: <b>140</b></li>
     *   <li>Easting: <b>567313.287</b></li>
     *   <li>Northing: <b>-6404311.163</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5109-06 : FORWARD")
    public void GIGS_5109_06() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-60, 140};
        final double[] destinationPoint = new double[]{567313.287, -6404311.163};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5109-07” for Albers calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5109-07</b></li>
     *   <li>Latitude: <b>-80</b></li>
     *   <li>Longitude: <b>140</b></li>
     *   <li>Easting: <b>486878.674</b></li>
     *   <li>Northing: <b>-7687130.029</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5109-07 : REVERSE")
    public void GIGS_5109_07() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{486878.674, -7687130.029};
        final double[] destinationPoint = new double[]{-80, 140};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5109-08” for Albers calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5109-08</b></li>
     *   <li>Latitude: <b>-60</b></li>
     *   <li>Longitude: <b>120</b></li>
     *   <li>Easting: <b>-850274.747</b></li>
     *   <li>Northing: <b>-6426505.132</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5109-08 : REVERSE")
    public void GIGS_5109_08() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-850274.747, -6426505.132};
        final double[] destinationPoint = new double[]{-60, 120};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5109-09” for Albers calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5109-09</b></li>
     *   <li>Latitude: <b>-60</b></li>
     *   <li>Longitude: <b>130</b></li>
     *   <li>Easting: <b>-141915.257</b></li>
     *   <li>Northing: <b>-6387653.78</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5109-09 : FORWARD")
    public void GIGS_5109_09() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-60, 130};
        final double[] destinationPoint = new double[]{-141915.257, -6387653.78};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5109-10” for Albers calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5109-10</b></li>
     *   <li>Latitude: <b>-60</b></li>
     *   <li>Longitude: <b>140</b></li>
     *   <li>Easting: <b>567313.287</b></li>
     *   <li>Northing: <b>-6404311.163</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5109-10 : REVERSE")
    public void GIGS_5109_10() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{567313.287, -6404311.163};
        final double[] destinationPoint = new double[]{-60, 140};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5109-11” for Albers calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5109-11</b></li>
     *   <li>Latitude: <b>-60</b></li>
     *   <li>Longitude: <b>150</b></li>
     *   <li>Easting: <b>1273067.747</b></li>
     *   <li>Northing: <b>-6476375.276</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5109-11 : FORWARD")
    public void GIGS_5109_11() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-60, 150};
        final double[] destinationPoint = new double[]{1273067.747, -6476375.276};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5109-12” for Albers calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5109-12</b></li>
     *   <li>Latitude: <b>-60</b></li>
     *   <li>Longitude: <b>160</b></li>
     *   <li>Easting: <b>1971026.264</b></li>
     *   <li>Northing: <b>-6603404.818</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5109-12 : REVERSE")
    public void GIGS_5109_12() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{1971026.264, -6603404.818};
        final double[] destinationPoint = new double[]{-60, 160};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5109-13” for Albers calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5109-13</b></li>
     *   <li>Latitude: <b>-60</b></li>
     *   <li>Longitude: <b>170</b></li>
     *   <li>Easting: <b>2656914.716</b></li>
     *   <li>Northing: <b>-6784621.89</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: Round Trip calculation point.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5109-13 : FORWARD")
    public void GIGS_5109_13() throws TransformException {
        isRTC = true;
        isForward = true;
        final double[] originPoint = new double[]{-60, 170};
        final double[] destinationPoint = new double[]{2656914.716, -6784621.89};
        convertAndVerifyRoundTripPoint(originPoint, destinationPoint);
    }
}
