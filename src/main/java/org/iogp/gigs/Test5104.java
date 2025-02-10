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
 *  Verifies the software’s capabilities to perform conversions for the Oblique Stereographic map projection.
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
 *   <td><a href="https://github.com/IOGP-GIGS/GIGSTestDataset/blob/main/GIGSTestDatasetFiles/GIGS%205100%20Conversion%20test%20data/ASCII/GIGS_conv_5104_OblStereo_input.txt">{@code GIGS_conv_5104_OblStereo_input.txt}</a>
 * </tr><tr>
 *   <th>Tested API:</th>
 *   <td>{@link CoordinateOperationFactory#createOperation(CoordinateReferenceSystem, CoordinateReferenceSystem)}.</td>
 * </tr><tr>
 *   <th>Expected result:</th>
 *   <td>Results for the forward and reverse calculations should agree to within 0.05m (50mm) or 0.0000006°
 *   of the Test Data. See file GIGS_conv_5104_OblStereo_output..
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
 * public class MyTest extends Test5104 {
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
@DisplayName("Oblique stereographic")
public class Test5104 extends Series5100 {

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
    public Test5104(final Factories factories) throws FactoryException {
        super(factories, 0.05, 0.0000006, 0.006, 0.00000006);
    }

    /**
     * Method used in class instantiation to initialise the geographic CRS and the projected CRS
     * used in this test class.
     * <p>Geographic CRS : GIGS CRS Code 64006; GIGS geogCRS C; Amersfoort; decimal degree; EPSG CRS code 4289
     * <p>ProjectedCRS : GIGS CRS Code 62011; GIGS projCRS C4; Amersfoort / RD New; metre; EPSG CRS code 28992
     *
     * @throws FactoryException if an error occurred while creating a CRS.
     */
    @Override
    protected void createCRSs() throws FactoryException {
        createGeogCRS(Test3205::GIGS_64006);
        createProjCRS(Test3207::GIGS_62011);
    }

    /**
     * Tests “GIGS-5104-01” for OblStereo calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5104-01</b></li>
     *   <li>Latitude: <b>58</b></li>
     *   <li>Longitude: <b>5</b></li>
     *   <li>Easting: <b>132023.27</b></li>
     *   <li>Northing: <b>1114054.872</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5104-01 : REVERSE")
    public void GIGS_5104_01() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{132023.27, 1114054.872};
        final double[] destinationPoint = new double[]{58, 5};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5104-02” for OblStereo calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5104-02</b></li>
     *   <li>Latitude: <b>57</b></li>
     *   <li>Longitude: <b>5</b></li>
     *   <li>Easting: <b>131405.466</b></li>
     *   <li>Northing: <b>1002468.081</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5104-02 : FORWARD")
    public void GIGS_5104_02() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{57, 5};
        final double[] destinationPoint = new double[]{131405.466, 1002468.081};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5104-03” for OblStereo calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5104-03</b></li>
     *   <li>Latitude: <b>56</b></li>
     *   <li>Longitude: <b>5</b></li>
     *   <li>Easting: <b>130792.264</b></li>
     *   <li>Northing: <b>890981.281</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5104-03 : REVERSE")
    public void GIGS_5104_03() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{130792.264, 890981.281};
        final double[] destinationPoint = new double[]{56, 5};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5104-04” for OblStereo calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5104-04</b></li>
     *   <li>Latitude: <b>55</b></li>
     *   <li>Longitude: <b>5</b></li>
     *   <li>Easting: <b>130183.562</b></li>
     *   <li>Northing: <b>779577.697</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5104-04 : FORWARD")
    public void GIGS_5104_04() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{55, 5};
        final double[] destinationPoint = new double[]{130183.562, 779577.697};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5104-05” for OblStereo calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5104-05</b></li>
     *   <li>Latitude: <b>54</b></li>
     *   <li>Longitude: <b>5</b></li>
     *   <li>Easting: <b>129579.261</b></li>
     *   <li>Northing: <b>668240.578</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5104-05 : REVERSE")
    public void GIGS_5104_05() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{129579.261, 668240.578};
        final double[] destinationPoint = new double[]{54, 5};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5104-06” for OblStereo calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5104-06</b></li>
     *   <li>Latitude: <b>53</b></li>
     *   <li>Longitude: <b>5</b></li>
     *   <li>Easting: <b>128979.263</b></li>
     *   <li>Northing: <b>556953.19</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5104-06 : FORWARD")
    public void GIGS_5104_06() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{53, 5};
        final double[] destinationPoint = new double[]{128979.263, 556953.19};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5104-07” for OblStereo calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5104-07</b></li>
     *   <li>Latitude: <b>52.15616056</b></li>
     *   <li>Longitude: <b>5.38763889</b></li>
     *   <li>Easting: <b>155000</b></li>
     *   <li>Northing: <b>463000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5104-07 : REVERSE")
    public void GIGS_5104_07() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{155000, 463000};
        final double[] destinationPoint = new double[]{52.15616056, 5.38763889};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5104-08” for OblStereo calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5104-08</b></li>
     *   <li>Latitude: <b>51</b></li>
     *   <li>Longitude: <b>4</b></li>
     *   <li>Easting: <b>57605.946</b></li>
     *   <li>Northing: <b>335312.662</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5104-08 : FORWARD")
    public void GIGS_5104_08() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{51, 4};
        final double[] destinationPoint = new double[]{57605.946, 335312.662};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5104-09” for OblStereo calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5104-09</b></li>
     *   <li>Latitude: <b>50</b></li>
     *   <li>Longitude: <b>4</b></li>
     *   <li>Easting: <b>55502.306</b></li>
     *   <li>Northing: <b>224086.514</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5104-09 : REVERSE")
    public void GIGS_5104_09() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{55502.306, 224086.514};
        final double[] destinationPoint = new double[]{50, 4};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5104-10” for OblStereo calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5104-10</b></li>
     *   <li>Latitude: <b>49</b></li>
     *   <li>Longitude: <b>4</b></li>
     *   <li>Easting: <b>53412.761</b></li>
     *   <li>Northing: <b>112842.732</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5104-10 : FORWARD")
    public void GIGS_5104_10() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{49, 4};
        final double[] destinationPoint = new double[]{53412.761, 112842.732};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5104-11” for OblStereo calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5104-11</b></li>
     *   <li>Latitude: <b>47.97526111</b></li>
     *   <li>Longitude: <b>3.31372806</b></li>
     *   <li>Easting: <b>0</b></li>
     *   <li>Northing: <b>0</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5104-11 : REVERSE")
    public void GIGS_5104_11() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{0, 0};
        final double[] destinationPoint = new double[]{47.97526111, 3.31372806};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5104-12” for OblStereo calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5104-12</b></li>
     *   <li>Latitude: <b>53</b></li>
     *   <li>Longitude: <b>3</b></li>
     *   <li>Easting: <b>-5253.063</b></li>
     *   <li>Northing: <b>559535.55</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5104-12 : REVERSE")
    public void GIGS_5104_12() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-5253.063, 559535.55};
        final double[] destinationPoint = new double[]{53, 3};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5104-13” for OblStereo calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5104-13</b></li>
     *   <li>Latitude: <b>53</b></li>
     *   <li>Longitude: <b>4</b></li>
     *   <li>Easting: <b>61856.776</b></li>
     *   <li>Northing: <b>557779.118</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5104-13 : FORWARD")
    public void GIGS_5104_13() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{53, 4};
        final double[] destinationPoint = new double[]{61856.776, 557779.118};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5104-14” for OblStereo calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5104-14</b></li>
     *   <li>Latitude: <b>53</b></li>
     *   <li>Longitude: <b>5</b></li>
     *   <li>Easting: <b>128979.263</b></li>
     *   <li>Northing: <b>556953.19</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5104-14 : REVERSE")
    public void GIGS_5104_14() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{128979.263, 556953.19};
        final double[] destinationPoint = new double[]{53, 5};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5104-15” for OblStereo calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5104-15</b></li>
     *   <li>Latitude: <b>53</b></li>
     *   <li>Longitude: <b>6</b></li>
     *   <li>Easting: <b>196105.283</b></li>
     *   <li>Northing: <b>557057.739</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5104-15 : FORWARD")
    public void GIGS_5104_15() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{53, 6};
        final double[] destinationPoint = new double[]{196105.283, 557057.739};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5104-16” for OblStereo calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5104-16</b></li>
     *   <li>Latitude: <b>53</b></li>
     *   <li>Longitude: <b>7</b></li>
     *   <li>Easting: <b>263225.722</b></li>
     *   <li>Northing: <b>558092.769</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5104-16 : REVERSE")
    public void GIGS_5104_16() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{263225.722, 558092.769};
        final double[] destinationPoint = new double[]{53, 7};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5104-17” for OblStereo calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5104-17</b></li>
     *   <li>Latitude: <b>53</b></li>
     *   <li>Longitude: <b>8</b></li>
     *   <li>Easting: <b>330331.464</b></li>
     *   <li>Northing: <b>560058.312</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5104-17 : FORWARD")
    public void GIGS_5104_17() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{53, 8};
        final double[] destinationPoint = new double[]{330331.464, 560058.312};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5104-18” for OblStereo calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5104-18</b></li>
     *   <li>Latitude: <b>53</b></li>
     *   <li>Longitude: <b>9</b></li>
     *   <li>Easting: <b>397413.385</b></li>
     *   <li>Northing: <b>562954.436</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5104-18 : REVERSE")
    public void GIGS_5104_18() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{397413.385, 562954.436};
        final double[] destinationPoint = new double[]{53, 9};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5104-19” for OblStereo calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5104-19</b></li>
     *   <li>Latitude: <b>53</b></li>
     *   <li>Longitude: <b>10</b></li>
     *   <li>Easting: <b>464462.348</b></li>
     *   <li>Northing: <b>566781.236</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5104-19 : FORWARD")
    public void GIGS_5104_19() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{53, 10};
        final double[] destinationPoint = new double[]{464462.348, 566781.236};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5104-20” for OblStereo calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5104-20</b></li>
     *   <li>Latitude: <b>53</b></li>
     *   <li>Longitude: <b>11</b></li>
     *   <li>Easting: <b>531469.202</b></li>
     *   <li>Northing: <b>571538.839</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * Remarks: Round Trip calculation point.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5104-20 : REVERSE")
    public void GIGS_5104_20() throws TransformException {
        isRTC = true;
        isForward = false;
        final double[] originPoint = new double[]{531469.202, 571538.839};
        final double[] destinationPoint = new double[]{53, 11};
        convertAndVerifyRoundTripPoint(originPoint, destinationPoint);
    }
}
