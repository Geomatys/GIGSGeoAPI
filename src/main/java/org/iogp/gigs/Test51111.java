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
import org.opengis.referencing.operation.*;
import org.opengis.util.FactoryException;


/**
 * Verifies the software’s capabilities to perform conversions for the Mercator (variant A) map projection.
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
 *   <td><a href="https://github.com/IOGP-GIGS/GIGSTestDataset/blob/main/GIGSTestDatasetFiles/GIGS%205100%20Conversion%20test%20data/ASCII/GIGS_conv_5111_MercA_input_part1.txt">{@code GIGS_conv_5111_MercA_input_part1.txt}</a>
 * </tr><tr>
 *   <th>Tested API:</th>
 *   <td>{@link CoordinateOperationFactory#createOperation(CoordinateReferenceSystem, CoordinateReferenceSystem)}.</td>
 * </tr><tr>
 *   <th>Expected result:</th>
 *   <td>Results for the forward and reverse calculations should agree to within 0.05m (50mm) or 0.0000006°
 *   of the Test Data. See file GIGS_conv_5111_MercA_output_part1.
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
 * public class MyTest extends Test51111 {
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
@DisplayName("Mercator (variant A)")
public class Test51111 extends Series5100 {

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
    public Test51111(final Factories factories) throws FactoryException {
        super(factories, 0.05, 0.0000006, 0.006, 0.00000006);
    }

    /**
     * Method used in class instantiation to initialise the geographic CRS and the projected CRS
     * used in this test class.
     * <p>Geographic CRS : GIGS CRS Code 64014; GIGS geogCRS L; Batavia; decimal degree; EPSG CRS code 4211
     * <p>ProjectedCRS : GIGS CRS Code 62037; GIGS projCRS L27; Batavia / NEIEZ; metre; EPSG CRS code 3001
     *
     * @throws FactoryException if an error occurred while creating a CRS.
     */
    @Override
    protected void createCRSs() throws FactoryException {
        createGeogCRS(Test3205::GIGS_64014);
        createProjCRS(Test3207::GIGS_62037);
    }

    /**
     * Convert the point to the output CRS.
     *
     * @param originPoint the point to convert to the destination CRS.
     * @return the result point from the conversion/transformation
     *
     * @throws TransformException if an error occurred while converting the given point.
     */
    double[] convertPoint(double[] originPoint) throws TransformException {
        final CoordinateOperation distop = isForward ? forwardCoOp : reverseCoOp;

        final MathTransform distTrs = distop.getMathTransform();
        final double[] distRes = new double[distTrs.getTargetDimensions()];
        distTrs.transform(originPoint, 0, distRes, 0, 1);

        if (!isForward && distRes[1] > 180) {
            distRes[1] = distRes[1] - 360;
        }
        return distRes;
    }

    /**
     * Tests “GIGS-5111-01” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-01</b></li>
     *   <li>Latitude: <b>77.6534822</b></li>
     *   <li>Longitude: <b>100.0876483</b></li>
     *   <li>Easting: <b>2800000</b></li>
     *   <li>Northing: <b>15000000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: Round Trip calculation point.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-01 : FORWARD")
    public void GIGS_5111_01() throws TransformException {
        isRTC = true;
        isForward = true;
        final double[] originPoint = new double[]{77.6534822, 100.0876483};
        final double[] destinationPoint = new double[]{2800000, 15000000};
        convertAndVerifyRoundTripPoint(originPoint, destinationPoint);
    }

    /**
     * Tests “GIGS-5111-02” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-02</b></li>
     *   <li>Latitude: <b>73.1442856</b></li>
     *   <li>Longitude: <b>100.0876483</b></li>
     *   <li>Easting: <b>2800000</b></li>
     *   <li>Northing: <b>13000000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-02 : REVERSE")
    public void GIGS_5111_02() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{2800000, 13000000};
        final double[] destinationPoint = new double[]{73.1442856, 100.0876483};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-03” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-03</b></li>
     *   <li>Latitude: <b>67.0518325</b></li>
     *   <li>Longitude: <b>100.0876483</b></li>
     *   <li>Easting: <b>2800000</b></li>
     *   <li>Northing: <b>11000000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-03 : FORWARD")
    public void GIGS_5111_03() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{67.0518325, 100.0876483};
        final double[] destinationPoint = new double[]{2800000, 11000000};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-04” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-04</b></li>
     *   <li>Latitude: <b>58.9140458</b></li>
     *   <li>Longitude: <b>100.0876483</b></li>
     *   <li>Easting: <b>2800000</b></li>
     *   <li>Northing: <b>9000000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-04 : REVERSE")
    public void GIGS_5111_04() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{2800000, 9000000};
        final double[] destinationPoint = new double[]{58.9140458, 100.0876483};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-05” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-05</b></li>
     *   <li>Latitude: <b>48.2638981</b></li>
     *   <li>Longitude: <b>100.0876483</b></li>
     *   <li>Easting: <b>2800000</b></li>
     *   <li>Northing: <b>7000000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-05 : FORWARD")
    public void GIGS_5111_05() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{48.2638981, 100.0876483};
        final double[] destinationPoint = new double[]{2800000, 7000000};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-06” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-06</b></li>
     *   <li>Latitude: <b>34.8029044</b></li>
     *   <li>Longitude: <b>100.0876483</b></li>
     *   <li>Easting: <b>2800000</b></li>
     *   <li>Northing: <b>5000000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-06 : REVERSE")
    public void GIGS_5111_06() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{2800000, 5000000};
        final double[] destinationPoint = new double[]{34.8029044, 100.0876483};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-07” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-07</b></li>
     *   <li>Latitude: <b>18.7048581</b></li>
     *   <li>Longitude: <b>100.0876483</b></li>
     *   <li>Easting: <b>2800000</b></li>
     *   <li>Northing: <b>3000000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-07 : FORWARD")
    public void GIGS_5111_07() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{18.7048581, 100.0876483};
        final double[] destinationPoint = new double[]{2800000, 3000000};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-08” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-08</b></li>
     *   <li>Latitude: <b>0.9071392</b></li>
     *   <li>Longitude: <b>100.0876483</b></li>
     *   <li>Easting: <b>2800000</b></li>
     *   <li>Northing: <b>1000000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-08 : REVERSE")
    public void GIGS_5111_08() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{2800000, 1000000};
        final double[] destinationPoint = new double[]{0.9071392, 100.0876483};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-09” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-09</b></li>
     *   <li>Latitude: <b>0</b></li>
     *   <li>Longitude: <b>110</b></li>
     *   <li>Easting: <b>3900000</b></li>
     *   <li>Northing: <b>900000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-09 : FORWARD")
    public void GIGS_5111_09() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{0, 110};
        final double[] destinationPoint = new double[]{3900000, 900000};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-10” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-10</b></li>
     *   <li>Latitude: <b>-0.9071392</b></li>
     *   <li>Longitude: <b>100.0876483</b></li>
     *   <li>Easting: <b>2800000</b></li>
     *   <li>Northing: <b>800000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-10 : REVERSE")
    public void GIGS_5111_10() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{2800000, 800000};
        final double[] destinationPoint = new double[]{-0.9071392, 100.0876483};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-11” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-11</b></li>
     *   <li>Latitude: <b>-1.8140483</b></li>
     *   <li>Longitude: <b>100.0876483</b></li>
     *   <li>Easting: <b>2800000</b></li>
     *   <li>Northing: <b>700000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-11 : FORWARD")
    public void GIGS_5111_11() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-1.8140483, 100.0876483};
        final double[] destinationPoint = new double[]{2800000, 700000};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-12” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-12</b></li>
     *   <li>Latitude: <b>-2</b></li>
     *   <li>Longitude: <b>100.0876483</b></li>
     *   <li>Easting: <b>2800000</b></li>
     *   <li>Northing: <b>679490.646</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-12 : REVERSE")
    public void GIGS_5111_12() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{2800000, 679490.646};
        final double[] destinationPoint = new double[]{-2, 100.0876483};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-13” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-13</b></li>
     *   <li>Latitude: <b>-3.6262553</b></li>
     *   <li>Longitude: <b>100.0876483</b></li>
     *   <li>Easting: <b>2800000</b></li>
     *   <li>Northing: <b>500000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-13 : FORWARD")
    public void GIGS_5111_13() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-3.6262553, 100.0876483};
        final double[] destinationPoint = new double[]{2800000, 500000};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-14” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-14</b></li>
     *   <li>Latitude: <b>-4.531095</b></li>
     *   <li>Longitude: <b>100.0876483</b></li>
     *   <li>Easting: <b>2800000</b></li>
     *   <li>Northing: <b>400000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-14 : REVERSE")
    public void GIGS_5111_14() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{2800000, 400000};
        final double[] destinationPoint = new double[]{-4.531095, 100.0876483};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-15” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-15</b></li>
     *   <li>Latitude: <b>-5.4347892</b></li>
     *   <li>Longitude: <b>100.0876483</b></li>
     *   <li>Easting: <b>2800000</b></li>
     *   <li>Northing: <b>300000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-15 : FORWARD")
    public void GIGS_5111_15() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-5.4347892, 100.0876483};
        final double[] destinationPoint = new double[]{2800000, 300000};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-16” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-16</b></li>
     *   <li>Latitude: <b>-6.3371111</b></li>
     *   <li>Longitude: <b>100.0876483</b></li>
     *   <li>Easting: <b>2800000</b></li>
     *   <li>Northing: <b>200000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-16 : REVERSE")
    public void GIGS_5111_16() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{2800000, 200000};
        final double[] destinationPoint = new double[]{-6.3371111, 100.0876483};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-17” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-17</b></li>
     *   <li>Latitude: <b>-7.2378372</b></li>
     *   <li>Longitude: <b>100.0876483</b></li>
     *   <li>Easting: <b>2800000</b></li>
     *   <li>Northing: <b>100000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-17 : FORWARD")
    public void GIGS_5111_17() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-7.2378372, 100.0876483};
        final double[] destinationPoint = new double[]{2800000, 100000};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-18” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-18</b></li>
     *   <li>Latitude: <b>-8.136745</b></li>
     *   <li>Longitude: <b>74.8562083</b></li>
     *   <li>Easting: <b>0</b></li>
     *   <li>Northing: <b>0</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-18 : REVERSE")
    public void GIGS_5111_18() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{0, 0};
        final double[] destinationPoint = new double[]{-8.136745, 74.8562083};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-19” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-19</b></li>
     *   <li>Latitude: <b>-2</b></li>
     *   <li>Longitude: <b>-71</b></li>
     *   <li>Easting: <b>23764105.84</b></li>
     *   <li>Northing: <b>679490.646</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-19 : REVERSE")
    public void GIGS_5111_19() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{23764105.84, 679490.646};
        final double[] destinationPoint = new double[]{-2, -71};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-20” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-20</b></li>
     *   <li>Latitude: <b>-2</b></li>
     *   <li>Longitude: <b>-90</b></li>
     *   <li>Easting: <b>21655625.33</b></li>
     *   <li>Northing: <b>679490.646</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-20 : FORWARD")
    public void GIGS_5111_20() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-2, -90};
        final double[] destinationPoint = new double[]{21655625.33, 679490.646};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-21” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-21</b></li>
     *   <li>Latitude: <b>-2</b></li>
     *   <li>Longitude: <b>-120</b></li>
     *   <li>Easting: <b>18326445.58</b></li>
     *   <li>Northing: <b>679490.646</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-21 : REVERSE")
    public void GIGS_5111_21() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{18326445.58, 679490.646};
        final double[] destinationPoint = new double[]{-2, -120};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-22” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-22</b></li>
     *   <li>Latitude: <b>-2</b></li>
     *   <li>Longitude: <b>-150</b></li>
     *   <li>Easting: <b>14997265.83</b></li>
     *   <li>Northing: <b>679490.646</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-22 : FORWARD")
    public void GIGS_5111_22() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-2, -150};
        final double[] destinationPoint = new double[]{14997265.83, 679490.646};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-23” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-23</b></li>
     *   <li>Latitude: <b>-2</b></li>
     *   <li>Longitude: <b>180</b></li>
     *   <li>Easting: <b>11668086.08</b></li>
     *   <li>Northing: <b>679490.646</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-23 : REVERSE")
    public void GIGS_5111_23() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{11668086.08, 679490.646};
        final double[] destinationPoint = new double[]{-2, 180};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-24” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-24</b></li>
     *   <li>Latitude: <b>-2</b></li>
     *   <li>Longitude: <b>150</b></li>
     *   <li>Easting: <b>8338906.333</b></li>
     *   <li>Northing: <b>679490.646</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-24 : FORWARD")
    public void GIGS_5111_24() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-2, 150};
        final double[] destinationPoint = new double[]{8338906.333, 679490.646};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-25” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-25</b></li>
     *   <li>Latitude: <b>-2</b></li>
     *   <li>Longitude: <b>120</b></li>
     *   <li>Easting: <b>5009726.583</b></li>
     *   <li>Northing: <b>679490.646</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-25 : REVERSE")
    public void GIGS_5111_25() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{5009726.583, 679490.646};
        final double[] destinationPoint = new double[]{-2, 120};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-26” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-26</b></li>
     *   <li>Latitude: <b>-2</b></li>
     *   <li>Longitude: <b>110</b></li>
     *   <li>Easting: <b>3900000</b></li>
     *   <li>Northing: <b>679490.646</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-26 : FORWARD")
    public void GIGS_5111_26() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-2, 110};
        final double[] destinationPoint = new double[]{3900000, 679490.646};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-27” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-27</b></li>
     *   <li>Latitude: <b>-2</b></li>
     *   <li>Longitude: <b>106.8077194</b></li>
     *   <li>Easting: <b>3545744.141</b></li>
     *   <li>Northing: <b>679490.646</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-27 : REVERSE")
    public void GIGS_5111_27() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{3545744.141, 679490.646};
        final double[] destinationPoint = new double[]{-2, 106.8077194};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-28” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-28</b></li>
     *   <li>Latitude: <b>-2</b></li>
     *   <li>Longitude: <b>100.0876483</b></li>
     *   <li>Easting: <b>2800000</b></li>
     *   <li>Northing: <b>679490.646</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-28 : FORWARD")
    public void GIGS_5111_28() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-2, 100.0876483};
        final double[] destinationPoint = new double[]{2800000, 679490.646};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-29” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-29</b></li>
     *   <li>Latitude: <b>-2</b></li>
     *   <li>Longitude: <b>90</b></li>
     *   <li>Easting: <b>1680546.833</b></li>
     *   <li>Northing: <b>679490.646</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-29 : REVERSE")
    public void GIGS_5111_29() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{1680546.833, 679490.646};
        final double[] destinationPoint = new double[]{-2, 90};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-30” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-30</b></li>
     *   <li>Latitude: <b>-2</b></li>
     *   <li>Longitude: <b>60</b></li>
     *   <li>Easting: <b>-1648632.916</b></li>
     *   <li>Northing: <b>679490.646</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-30 : FORWARD")
    public void GIGS_5111_30() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-2, 60};
        final double[] destinationPoint = new double[]{-1648632.916, 679490.646};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-31” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-31</b></li>
     *   <li>Latitude: <b>-2</b></li>
     *   <li>Longitude: <b>30</b></li>
     *   <li>Easting: <b>-4977812.666</b></li>
     *   <li>Northing: <b>679490.646</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-31 : REVERSE")
    public void GIGS_5111_31() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-4977812.666, 679490.646};
        final double[] destinationPoint = new double[]{-2, 30};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-32” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-32</b></li>
     *   <li>Latitude: <b>-2</b></li>
     *   <li>Longitude: <b>0</b></li>
     *   <li>Easting: <b>-8306992.416</b></li>
     *   <li>Northing: <b>679490.646</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-32 : FORWARD")
    public void GIGS_5111_32() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-2, 0};
        final double[] destinationPoint = new double[]{-8306992.416, 679490.646};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-33” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-33</b></li>
     *   <li>Latitude: <b>-2</b></li>
     *   <li>Longitude: <b>-30</b></li>
     *   <li>Easting: <b>-11636172.17</b></li>
     *   <li>Northing: <b>679490.646</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-33 : REVERSE")
    public void GIGS_5111_33() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-11636172.17, 679490.646};
        final double[] destinationPoint = new double[]{-2, -30};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-34” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-34</b></li>
     *   <li>Latitude: <b>-2</b></li>
     *   <li>Longitude: <b>-60</b></li>
     *   <li>Easting: <b>-14965351.92</b></li>
     *   <li>Northing: <b>679490.646</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-34 : FORWARD")
    public void GIGS_5111_34() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-2, -60};
        final double[] destinationPoint = new double[]{-14965351.92, 679490.646};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-35” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-35</b></li>
     *   <li>Latitude: <b>-2</b></li>
     *   <li>Longitude: <b>-69</b></li>
     *   <li>Easting: <b>-15964105.84</b></li>
     *   <li>Northing: <b>679490.646</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-35 : REVERSE")
    public void GIGS_5111_35() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-15964105.84, 679490.646};
        final double[] destinationPoint = new double[]{-2, -69};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }
}
