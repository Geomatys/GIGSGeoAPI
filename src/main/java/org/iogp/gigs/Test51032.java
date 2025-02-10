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
 *   <td><a href="https://github.com/IOGP-GIGS/GIGSTestDataset/blob/main/GIGSTestDatasetFiles/GIGS%205100%20Conversion%20test%20data/ASCII/GIGS_conv_5103_LCC2_input_part2.txt">{@code GIGS_conv_5103_LCC2_input_part2.txt}</a>
 * </tr><tr>
 *   <th>Tested API:</th>
 *   <td>{@link CoordinateOperationFactory#createOperation(CoordinateReferenceSystem, CoordinateReferenceSystem)}.</td>
 * </tr><tr>
 *   <th>Expected result:</th>
 *   <td>Results for the forward and reverse calculations should agree to within 0.1 ftUS/ft (1.2in) or 0.0000003°
 *   of the Test Data. See file GIGS_conv_5103_LCC2_output_part2.
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
 * public class MyTest extends Test51032 {
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
public class Test51032 extends Series5100 {

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
    public Test51032(final Factories factories) throws FactoryException {
        super(factories, 0.03, 0.0000003, 0.006, 0.00000006);
    }

    /**
     * Method used in class instantiation to initialise the geographic CRS and the projected CRS
     * used in this test class.
     * <p>Geographic CRS : GIGS CRS Code 64010; GIGS geogCRS G; NAD83(HARN); decimal degree; EPSG CRS code 4152
     * <p>ProjectedCRS : GIGS CRS Code 62024; GIGS projCRS G17; NAD83(HARN) / Utah North (ft); foot; EPSG CRS code 2921
     *
     * @throws FactoryException if an error occurred while creating a CRS.
     */
    @Override
    protected void createCRSs() throws FactoryException {
        createGeogCRS(Test3205::GIGS_64010);
        createProjCRS(Test3207::GIGS_62024);
    }

    /**
     * Tests “GIGS-5103-21” for LCC2 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5103-21</b></li>
     *   <li>Latitude: <b>49</b></li>
     *   <li>Longitude: <b>-110</b></li>
     *   <li>Easting: <b>2003937.274</b></li>
     *   <li>Northing: <b>6452491.702</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5103-21 : FORWARD")
    public void GIGS_5103_21() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{49, -110};
        final double[] destinationPoint = new double[]{2003937.274, 6452491.702};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5103-22” for LCC2 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5103-22</b></li>
     *   <li>Latitude: <b>47</b></li>
     *   <li>Longitude: <b>-110</b></li>
     *   <li>Easting: <b>2016621.93</b></li>
     *   <li>Northing: <b>5717728.614</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5103-22 : REVERSE")
    public void GIGS_5103_22() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{2016621.93, 5717728.614};
        final double[] destinationPoint = new double[]{47, -110};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5103-23” for LCC2 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5103-23</b></li>
     *   <li>Latitude: <b>45</b></li>
     *   <li>Longitude: <b>-110</b></li>
     *   <li>Easting: <b>2029255.572</b></li>
     *   <li>Northing: <b>4985920.564</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5103-23 : FORWARD")
    public void GIGS_5103_23() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{45, -110};
        final double[] destinationPoint = new double[]{2029255.572, 4985920.564};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5103-24” for LCC2 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5103-24</b></li>
     *   <li>Latitude: <b>43</b></li>
     *   <li>Longitude: <b>-110</b></li>
     *   <li>Easting: <b>2041855.081</b></li>
     *   <li>Northing: <b>4256089.737</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5103-24 : REVERSE")
    public void GIGS_5103_24() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{2041855.081, 4256089.737};
        final double[] destinationPoint = new double[]{43, -110};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5103-25” for LCC2 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5103-25</b></li>
     *   <li>Latitude: <b>41</b></li>
     *   <li>Longitude: <b>-110</b></li>
     *   <li>Easting: <b>2054436.569</b></li>
     *   <li>Northing: <b>3527302.727</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5103-25 : FORWARD")
    public void GIGS_5103_25() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{41, -110};
        final double[] destinationPoint = new double[]{2054436.569, 3527302.727};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5103-26” for LCC2 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5103-26</b></li>
     *   <li>Latitude: <b>41</b></li>
     *   <li>Longitude: <b>-110</b></li>
     *   <li>Easting: <b>2054436.569</b></li>
     *   <li>Northing: <b>3527302.727</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5103-26 : REVERSE")
    public void GIGS_5103_26() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{2054436.569, 3527302.727};
        final double[] destinationPoint = new double[]{41, -110};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5103-27” for LCC2 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5103-27</b></li>
     *   <li>Latitude: <b>41</b></li>
     *   <li>Longitude: <b>-108</b></li>
     *   <li>Easting: <b>2606245.515</b></li>
     *   <li>Northing: <b>3543182.547</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5103-27 : FORWARD")
    public void GIGS_5103_27() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{41, -108};
        final double[] destinationPoint = new double[]{2606245.515, 3543182.547};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5103-28” for LCC2 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5103-28</b></li>
     *   <li>Latitude: <b>41</b></li>
     *   <li>Longitude: <b>-106</b></li>
     *   <li>Easting: <b>3157542.857</b></li>
     *   <li>Northing: <b>3571757.391</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5103-28 : REVERSE")
    public void GIGS_5103_28() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{3157542.857, 3571757.391};
        final double[] destinationPoint = new double[]{41, -106};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5103-29” for LCC2 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5103-29</b></li>
     *   <li>Latitude: <b>41</b></li>
     *   <li>Longitude: <b>-104</b></li>
     *   <li>Easting: <b>3708036.571</b></li>
     *   <li>Northing: <b>3613012.123</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5103-29 : FORWARD")
    public void GIGS_5103_29() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{41, -104};
        final double[] destinationPoint = new double[]{3708036.571, 3613012.123};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5103-30” for LCC2 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5103-30</b></li>
     *   <li>Latitude: <b>41</b></li>
     *   <li>Longitude: <b>-102</b></li>
     *   <li>Easting: <b>4257435.056</b></li>
     *   <li>Northing: <b>3666924.889</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5103-30 : REVERSE")
    public void GIGS_5103_30() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{4257435.056, 3666924.889};
        final double[] destinationPoint = new double[]{41, -102};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }
}
