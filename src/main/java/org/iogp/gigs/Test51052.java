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
 *   <td><a href="https://github.com/IOGP-GIGS/GIGSTestDataset/blob/main/GIGSTestDatasetFiles/GIGS%205100%20Conversion%20test%20data/ASCII/GIGS_conv_5105_HOM-B_input_part2.txt">{@code GIGS_conv_5105_HOM-B_input_part2.txt}</a>
 * </tr><tr>
 *   <th>Tested API:</th>
 *   <td>{@link CoordinateOperationFactory#createOperation(CoordinateReferenceSystem, CoordinateReferenceSystem)}.</td>
 * </tr><tr>
 *   <th>Expected result:</th>
 *   <td>Results for the forward and reverse calculations should agree to within 0.05m (50mm) or 0.0000006°
 *   of the Test Data. See file GIGS_conv_5105_HOM-B_output_part2.
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
 * public class MyTest extends Test51052 {
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
public class Test51052 extends Series5100 {

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
    public Test51052(final Factories factories) throws FactoryException {
        super(factories, 0.05, 0.0000006, 0.006, 0.00000006);
    }

    /**
     * Method used in class instantiation to initialise the geographic CRS and the projected CRS
     * used in this test class.
     * <p>Geographic CRS : GIGS CRS Code 64015; GIGS geogCRS K; HD72; decimal degree; EPSG CRS code 4237
     * <p>ProjectedCRS : GIGS CRS Code 62036; GIGS projCRS K26; HD72 / EOV; metre; EPSG CRS code 23700
     *
     * @throws FactoryException if an error occurred while creating a CRS.
     */
    @Override
    protected void createCRSs() throws FactoryException {
        createGeogCRS(Test3205::GIGS_64015);
        createProjCRS(Test3207::GIGS_62036);
    }

    /**
     * Tests “GIGS-5105-24” for HOM-B calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5105-24</b></li>
     *   <li>Latitude: <b>48.5</b></li>
     *   <li>Longitude: <b>16</b></li>
     *   <li>Easting: <b>424714.235</b></li>
     *   <li>Northing: <b>355124.6</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: Outside modern boundary of Hungary.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5105-24 : FORWARD")
    public void GIGS_5105_24() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{48.5, 16};
        final double[] destinationPoint = new double[]{424714.235, 355124.6};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5105-25” for HOM-B calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5105-25</b></li>
     *   <li>Latitude: <b>48</b></li>
     *   <li>Longitude: <b>17.2</b></li>
     *   <li>Easting: <b>512056.188</b></li>
     *   <li>Northing: <b>296756.716</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5105-25 : REVERSE")
    public void GIGS_5105_25() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{512056.188, 296756.716};
        final double[] destinationPoint = new double[]{48, 17.2};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5105-26” for HOM-B calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5105-26</b></li>
     *   <li>Latitude: <b>47.63613472</b></li>
     *   <li>Longitude: <b>17.58265056</b></li>
     *   <li>Easting: <b>539847.765</b></li>
     *   <li>Northing: <b>255701.086</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5105-26 : FORWARD")
    public void GIGS_5105_26() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{47.63613472, 17.58265056};
        final double[] destinationPoint = new double[]{539847.765, 255701.086};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5105-27” for HOM-B calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5105-27</b></li>
     *   <li>Latitude: <b>47.14439361</b></li>
     *   <li>Longitude: <b>19.04857167</b></li>
     *   <li>Easting: <b>650000</b></li>
     *   <li>Northing: <b>200000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5105-27 : REVERSE")
    public void GIGS_5105_27() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{650000, 200000};
        final double[] destinationPoint = new double[]{47.14439361, 19.04857167};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5105-28” for HOM-B calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5105-28</b></li>
     *   <li>Latitude: <b>46.87566833</b></li>
     *   <li>Longitude: <b>19.22342944</b></li>
     *   <li>Easting: <b>663329.053</b></li>
     *   <li>Northing: <b>170142.318</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5105-28 : FORWARD")
    public void GIGS_5105_28() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{46.87566833, 19.22342944};
        final double[] destinationPoint = new double[]{663329.053, 170142.318};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5105-29” for HOM-B calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5105-29</b></li>
     *   <li>Latitude: <b>46.37030111</b></li>
     *   <li>Longitude: <b>20.13574056</b></li>
     *   <li>Easting: <b>733651.455</b></li>
     *   <li>Northing: <b>114532.099</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5105-29 : REVERSE")
    public void GIGS_5105_29() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{733651.455, 114532.099};
        final double[] destinationPoint = new double[]{46.37030111, 20.13574056};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5105-30” for HOM-B calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5105-30</b></li>
     *   <li>Latitude: <b>45.7</b></li>
     *   <li>Longitude: <b>21.4</b></li>
     *   <li>Easting: <b>833148.855</b></li>
     *   <li>Northing: <b>42191.482</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: Outside modern boundary of Hungary.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5105-30 : FORWARD")
    public void GIGS_5105_30() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{45.7, 21.4};
        final double[] destinationPoint = new double[]{833148.855, 42191.482};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5105-31” for HOM-B calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5105-31</b></li>
     *   <li>Latitude: <b>49.3</b></li>
     *   <li>Longitude: <b>22.3</b></li>
     *   <li>Easting: <b>886565.935</b></li>
     *   <li>Northing: <b>444656.613</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: Outside modern boundary of Hungary.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5105-31 : REVERSE")
    public void GIGS_5105_31() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{886565.935, 444656.613};
        final double[] destinationPoint = new double[]{49.3, 22.3};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5105-32” for HOM-B calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5105-32</b></li>
     *   <li>Latitude: <b>48.48997472</b></li>
     *   <li>Longitude: <b>21.29419861</b></li>
     *   <li>Easting: <b>815999.993</b></li>
     *   <li>Northing: <b>351999.998</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5105-32 : FORWARD")
    public void GIGS_5105_32() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{48.48997472, 21.29419861};
        final double[] destinationPoint = new double[]{815999.993, 351999.998};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5105-33” for HOM-B calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5105-33</b></li>
     *   <li>Latitude: <b>46.87566833</b></li>
     *   <li>Longitude: <b>19.22342944</b></li>
     *   <li>Easting: <b>663329.053</b></li>
     *   <li>Northing: <b>170142.318</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5105-33 : REVERSE")
    public void GIGS_5105_33() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{663329.053, 170142.318};
        final double[] destinationPoint = new double[]{46.87566833, 19.22342944};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5105-34” for HOM-B calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5105-34</b></li>
     *   <li>Latitude: <b>46.06874639</b></li>
     *   <li>Longitude: <b>17.61915361</b></li>
     *   <li>Easting: <b>539403.958</b></li>
     *   <li>Northing: <b>81440.103</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5105-34 : FORWARD")
    public void GIGS_5105_34() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{46.06874639, 17.61915361};
        final double[] destinationPoint = new double[]{539403.958, 81440.103};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5105-35” for HOM-B calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5105-35</b></li>
     *   <li>Latitude: <b>45.5</b></li>
     *   <li>Longitude: <b>16.36</b></li>
     *   <li>Easting: <b>439836.709</b></li>
     *   <li>Northing: <b>20816.456</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: Outside modern boundary of Hungary.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5105-35 : REVERSE")
    public void GIGS_5105_35() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{439836.709, 20816.456};
        final double[] destinationPoint = new double[]{45.5, 16.36};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }
}
