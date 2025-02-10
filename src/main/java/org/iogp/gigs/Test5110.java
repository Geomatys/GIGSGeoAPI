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
 * Verifies the software’s capabilities to perform conversions for the Lambert Azimuthal Equal Area map projection.
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
 *   <td><a href="https://github.com/IOGP-GIGS/GIGSTestDataset/blob/main/GIGSTestDatasetFiles/GIGS%205100%20Conversion%20test%20data/ASCII/GIGS_conv_5110_LAEA_input.txt">{@code GIGS_conv_5110_LAEA_input.txt}</a>
 * </tr><tr>
 *   <th>Tested API:</th>
 *   <td>{@link CoordinateOperationFactory#createOperation(CoordinateReferenceSystem, CoordinateReferenceSystem)}.</td>
 * </tr><tr>
 *   <th>Expected result:</th>
 *   <td>Results for the forward and reverse calculations should agree to within 0.05m (50mm) or 0.0000006°
 *   of the Test Data. See file GIGS_conv_5110_LAEA_output.
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
 * public class MyTest extends Test5110 {
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
@DisplayName("Lambert Azimuthal Equal Area")
public class Test5110 extends Series5100 {

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
    public Test5110(final Factories factories) throws FactoryException {
        super(factories, 0.05, 0.0000006, 0.006, 0.00000006);
    }

    /**
     * Method used in class instantiation to initialise the geographic CRS and the projected CRS
     * used in this test class.
     * <p>Geographic CRS : GIGS CRS Code 64010; GIGS geogCRS G; ETRS89; decimal degree; EPSG CRS code 4258
     * <p>ProjectedCRS : GIGS CRS Code 62023; GIGS projCRS G16; ETRS89-extended / LAEA Europe; metre; EPSG CRS code 3035
     *
     * @throws FactoryException if an error occurred while creating a CRS.
     */
    @Override
    protected void createCRSs() throws FactoryException {
        createGeogCRS(Test3205::GIGS_64010);
        createProjCRS(Test3207::GIGS_62023);
    }

    /**
     * Tests “GIGS-5110-01” for Albers calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5110-01</b></li>
     *   <li>Latitude: <b>70</b></li>
     *   <li>Longitude: <b>5</b></li>
     *   <li>Easting: <b>5214090.649</b></li>
     *   <li>Northing: <b>4127824.658</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5110-01 : FORWARD")
    public void GIGS_5110_01() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{70, 5};
        final double[] destinationPoint = new double[]{5214090.649, 4127824.658};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5110-02” for Albers calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5110-02</b></li>
     *   <li>Latitude: <b>60</b></li>
     *   <li>Longitude: <b>5</b></li>
     *   <li>Easting: <b>4109791.66</b></li>
     *   <li>Northing: <b>4041548.125</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5110-02 : REVERSE")
    public void GIGS_5110_02() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{4109791.66, 4041548.125};
        final double[] destinationPoint = new double[]{60, 5};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5110-03” for Albers calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5110-03</b></li>
     *   <li>Latitude: <b>50</b></li>
     *   <li>Longitude: <b>5</b></li>
     *   <li>Easting: <b>2999718.853</b></li>
     *   <li>Northing: <b>3962799.451</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5110-03 : FORWARD")
    public void GIGS_5110_03() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{50, 5};
        final double[] destinationPoint = new double[]{2999718.853, 3962799.451};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5110-04” for Albers calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5110-04</b></li>
     *   <li>Latitude: <b>40</b></li>
     *   <li>Longitude: <b>5</b></li>
     *   <li>Easting: <b>1892578.962</b></li>
     *   <li>Northing: <b>3892127.02</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5110-04 : REVERSE")
    public void GIGS_5110_04() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{1892578.962, 3892127.02};
        final double[] destinationPoint = new double[]{40, 5};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5110-05” for Albers calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5110-05</b></li>
     *   <li>Latitude: <b>30</b></li>
     *   <li>Longitude: <b>5</b></li>
     *   <li>Easting: <b>796781.677</b></li>
     *   <li>Northing: <b>3830117.902</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5110-05 : FORWARD")
    public void GIGS_5110_05() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{30, 5};
        final double[] destinationPoint = new double[]{796781.677, 3830117.902};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5110-06” for Albers calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5110-06</b></li>
     *   <li>Latitude: <b>52</b></li>
     *   <li>Longitude: <b>10</b></li>
     *   <li>Easting: <b>3210000</b></li>
     *   <li>Northing: <b>4321000</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5110-06 : FORWARD")
    public void GIGS_5110_06() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{52, 10};
        final double[] destinationPoint = new double[]{3210000, 4321000};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5110-07” for Albers calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5110-07</b></li>
     *   <li>Latitude: <b>50</b></li>
     *   <li>Longitude: <b>0</b></li>
     *   <li>Easting: <b>3036305.967</b></li>
     *   <li>Northing: <b>3606514.431</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5110-07 : REVERSE")
    public void GIGS_5110_07() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{3036305.967, 3606514.431};
        final double[] destinationPoint = new double[]{50, 0};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5110-08” for Albers calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5110-08</b></li>
     *   <li>Latitude: <b>50</b></li>
     *   <li>Longitude: <b>3</b></li>
     *   <li>Easting: <b>3011432.894</b></li>
     *   <li>Northing: <b>3819948.288</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5110-08 : FORWARD")
    public void GIGS_5110_08() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{50, 3};
        final double[] destinationPoint = new double[]{3011432.894, 3819948.288};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5110-09” for Albers calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5110-09</b></li>
     *   <li>Latitude: <b>50</b></li>
     *   <li>Longitude: <b>5</b></li>
     *   <li>Easting: <b>2999718.853</b></li>
     *   <li>Northing: <b>3962799.451</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5110-09 : REVERSE")
    public void GIGS_5110_09() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{2999718.853, 3962799.451};
        final double[] destinationPoint = new double[]{50, 5};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5110-10” for Albers calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5110-10</b></li>
     *   <li>Latitude: <b>50</b></li>
     *   <li>Longitude: <b>8</b></li>
     *   <li>Easting: <b>2989464.315</b></li>
     *   <li>Northing: <b>4177612.521</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5110-10 : FORWARD")
    public void GIGS_5110_10() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{50, 8};
        final double[] destinationPoint = new double[]{2989464.315, 4177612.521};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5110-11” for Albers calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5110-11</b></li>
     *   <li>Latitude: <b>50</b></li>
     *   <li>Longitude: <b>10</b></li>
     *   <li>Easting: <b>2987510.567</b></li>
     *   <li>Northing: <b>4321000</b></li>
     *   <li>Transect: <b>C</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: Round Trip calculation point.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5110-11 : REVERSE")
    public void GIGS_5110_11() throws TransformException {
        isRTC = true;
        isForward = false;
        final double[] originPoint = new double[]{2987510.567, 4321000};
        final double[] destinationPoint = new double[]{50, 10};
        convertAndVerifyRoundTripPoint(originPoint, destinationPoint);
    }
}
