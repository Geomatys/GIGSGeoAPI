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
 * Verifies the software’s capabilities to perform conversions for the American Polyconic map projection.
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
 *   <td><a href="https://github.com/IOGP-GIGS/GIGSTestDataset/blob/main/GIGSTestDatasetFiles/GIGS%205100%20Conversion%20test%20data/ASCII/GIGS_conv_5107_AmPolyC_input.txt">{@code GIGS_conv_5107_AmPolyC_input.txt}</a>
 * </tr><tr>
 *   <th>Tested API:</th>
 *   <td>{@link CoordinateOperationFactory#createOperation(CoordinateReferenceSystem, CoordinateReferenceSystem)}.</td>
 * </tr><tr>
 *   <th>Expected result:</th>
 *   <td>Results for the forward and reverse calculations should agree to within 0.05m (50mm) or 0.0000006°
 *   of the Test Data. See file GIGS_conv_5107_AmPolyC_output.
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
 * public class MyTest extends Test5107 {
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
@DisplayName("American Polyconic")
public class Test5107 extends Series5100 {

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
    public Test5107(final Factories factories) throws FactoryException {
        super(factories, 0.05, 0.0000006, 0.006, 0.00000006);
    }

    /**
     * Method used in class instantiation to initialise the geographic CRS and the projected CRS
     * used in this test class.
     * <p>Geographic CRS : GIGS CRS Code 64010; GIGS geogCRS G; SIRGAS 2000; decimal degree; EPSG CRS code 4674
     * <p>ProjectedCRS : GIGS CRS Code 62019; GIGS projCRS G12; SIRGAS 2000 / Brazil Polyconic; metre; EPSG CRS code 5880
     *
     * @throws FactoryException if an error occurred while creating a CRS.
     */
    @Override
    protected void createCRSs() throws FactoryException {
        createGeogCRS(Test3205::GIGS_64010);
        createProjCRS(Test3207::GIGS_62019);
    }

    /**
     * Tests “GIGS-5107-01” for AmPolyC calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5107-01</b></li>
     *   <li>Latitude: <b>0</b></li>
     *   <li>Longitude: <b>-54</b></li>
     *   <li>Easting: <b>5000000</b></li>
     *   <li>Northing: <b>10000000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5107-01 : FORWARD")
    public void GIGS_5107_01() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{0, -54};
        final double[] destinationPoint = new double[]{5000000, 10000000};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5107-02” for AmPolyC calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5107-02</b></li>
     *   <li>Latitude: <b>6</b></li>
     *   <li>Longitude: <b>-45</b></li>
     *   <li>Easting: <b>5996378.71</b></li>
     *   <li>Northing: <b>10671650.056</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5107-02 : REVERSE")
    public void GIGS_5107_02() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{5996378.71, 10671650.056};
        final double[] destinationPoint = new double[]{6, -45};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5107-03” for AmPolyC calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5107-03</b></li>
     *   <li>Latitude: <b>0</b></li>
     *   <li>Longitude: <b>-45</b></li>
     *   <li>Easting: <b>6001875.417</b></li>
     *   <li>Northing: <b>10000000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5107-03 : REVERSE")
    public void GIGS_5107_03() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{6001875.417, 10000000};
        final double[] destinationPoint = new double[]{0, -45};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5107-04” for AmPolyC calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5107-04</b></li>
     *   <li>Latitude: <b>-6</b></li>
     *   <li>Longitude: <b>-45</b></li>
     *   <li>Easting: <b>5996378.71</b></li>
     *   <li>Northing: <b>9328349.944</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5107-04 : FORWARD")
    public void GIGS_5107_04() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-6, -45};
        final double[] destinationPoint = new double[]{5996378.71, 9328349.944};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5107-05” for AmPolyC calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5107-05</b></li>
     *   <li>Latitude: <b>-13</b></li>
     *   <li>Longitude: <b>-41</b></li>
     *   <li>Easting: <b>6409689.587</b></li>
     *   <li>Northing: <b>8526306.262</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5107-05 : FORWARD")
    public void GIGS_5107_05() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-13, -41};
        final double[] destinationPoint = new double[]{6409689.587, 8526306.262};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5107-06” for AmPolyC calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5107-06</b></li>
     *   <li>Latitude: <b>-20</b></li>
     *   <li>Longitude: <b>-38</b></li>
     *   <li>Easting: <b>6671808.92</b></li>
     *   <li>Northing: <b>7707735.73</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5107-06 : REVERSE")
    public void GIGS_5107_06() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{6671808.92, 7707735.73};
        final double[] destinationPoint = new double[]{-20, -38};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5107-07” for AmPolyC calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5107-07</b></li>
     *   <li>Latitude: <b>-24</b></li>
     *   <li>Longitude: <b>-37</b></li>
     *   <li>Easting: <b>6725584.492</b></li>
     *   <li>Northing: <b>7240461.996</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5107-07 : FORWARD")
    public void GIGS_5107_07() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-24, -37};
        final double[] destinationPoint = new double[]{6725584.492, 7240461.996};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5107-08” for AmPolyC calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5107-08</b></li>
     *   <li>Latitude: <b>-30</b></li>
     *   <li>Longitude: <b>-36</b></li>
     *   <li>Easting: <b>6729619.74</b></li>
     *   <li>Northing: <b>6543762.576</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5107-08 : REVERSE")
    public void GIGS_5107_08() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{6729619.74, 6543762.576};
        final double[] destinationPoint = new double[]{-30, -36};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5107-09” for AmPolyC calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5107-09</b></li>
     *   <li>Latitude: <b>-30</b></li>
     *   <li>Longitude: <b>-57</b></li>
     *   <li>Easting: <b>4710574.223</b></li>
     *   <li>Northing: <b>6676097.811</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5107-09 : FORWARD")
    public void GIGS_5107_09() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-30, -57};
        final double[] destinationPoint = new double[]{4710574.223, 6676097.811};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5107-10” for AmPolyC calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5107-10</b></li>
     *   <li>Latitude: <b>-29.36747667</b></li>
     *   <li>Longitude: <b>-54</b></li>
     *   <li>Easting: <b>5000000</b></li>
     *   <li>Northing: <b>6750000</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5107-10 : REVERSE")
    public void GIGS_5107_10() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{5000000, 6750000};
        final double[] destinationPoint = new double[]{-29.36747667, -54};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5107-11” for AmPolyC calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5107-11</b></li>
     *   <li>Latitude: <b>-27.5</b></li>
     *   <li>Longitude: <b>-47</b></li>
     *   <li>Easting: <b>5691318.147</b></li>
     *   <li>Northing: <b>6937461.051</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5107-11 : FORWARD")
    public void GIGS_5107_11() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-27.5, -47};
        final double[] destinationPoint = new double[]{5691318.147, 6937461.051};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5107-12” for AmPolyC calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5107-12</b></li>
     *   <li>Latitude: <b>-24</b></li>
     *   <li>Longitude: <b>-37</b></li>
     *   <li>Easting: <b>6725584.492</b></li>
     *   <li>Northing: <b>7240461.996</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5107-12 : REVERSE")
    public void GIGS_5107_12() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{6725584.492, 7240461.996};
        final double[] destinationPoint = new double[]{-24, -37};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5107-13” for AmPolyC calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5107-13</b></li>
     *   <li>Latitude: <b>-22.5</b></li>
     *   <li>Longitude: <b>-30</b></li>
     *   <li>Easting: <b>7458947.701</b></li>
     *   <li>Northing: <b>7313327.317</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: Round Trip calculation point.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5107-13 : FORWARD")
    public void GIGS_5107_13() throws TransformException {
        isRTC = true;
        isForward = true;
        final double[] originPoint = new double[]{-22.5, -30};
        final double[] destinationPoint = new double[]{7458947.701, 7313327.317};
        convertAndVerifyRoundTripPoint(originPoint, destinationPoint);
    }
}
