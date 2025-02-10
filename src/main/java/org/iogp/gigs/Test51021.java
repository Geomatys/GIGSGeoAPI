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
 * Verifies the software’s capabilities to perform conversions for the Lambert Conic Conformal (1SP) map projection.
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
 *   <td><a href="https://github.com/IOGP-GIGS/GIGSTestDataset/blob/main/GIGSTestDatasetFiles/GIGS%205100%20Conversion%20test%20data/ASCII/GIGS_conv_5102_LCC1_input_part1.txt">{@code GIGS_conv_5102_LCC1_input_part1.txt}</a>
 * </tr><tr>
 *   <th>Tested API:</th>
 *   <td>{@link CoordinateOperationFactory#createOperation(CoordinateReferenceSystem, CoordinateReferenceSystem)}.</td>
 * </tr><tr>
 *   <th>Expected result:</th>
 *   <td>Results for the forward and reverse calculations should agree to within 0.03m (30mm) or
 *   0.0000003° of the Test Data. See file GIGS_conv_5102_LCC1_output_part1.
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
 * public class MyTest extends Test51021 {
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
@DisplayName("Lambert Conic Conformal (1SP)")
public class Test51021 extends Series5100 {

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
    public Test51021(final Factories factories) throws FactoryException {
        super(factories, 0.03, 0.0000003, 0.006, 0.00000006);
    }

    /**
     * Method used in class instantiation to initialise the geographic CRS and the projected CRS
     * used in this test class.
     * <p>Geographic CRS : GIGS CRS Code 64020; GIGS geogCRS M; ED50; decimal degree; EPSG CRS code 4230
     * <p>ProjectedCRS : GIGS CRS Code 62035; GIGS projCRS M25; ED50 / France EuroLambert; metre; No direct EPSG equivalent
     *
     * @throws FactoryException if an error occurred while creating a CRS.
     */
    @Override
    protected void createCRSs() throws FactoryException {
        createGeogCRS(Test3205::GIGS_64020);
        createProjCRS(Test3207::GIGS_62035);
    }

    /**
     * Tests “GIGS-5102-01” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-01</b></li>
     *   <li>Latitude: <b>58</b></li>
     *   <li>Longitude: <b>5</b></li>
     *   <li>Easting: <b>760722.92</b></li>
     *   <li>Northing: <b>3457368.68</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: Round Trip calculation point.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-01 : FORWARD")
    public void GIGS_5102_01() throws TransformException {
        isRTC = true;
        isForward = true;
        final double[] originPoint = new double[]{58, 5};
        final double[] destinationPoint = new double[]{760722.92, 3457368.68};
        convertAndVerifyRoundTripPoint(originPoint, destinationPoint);
    }

    /**
     * Tests “GIGS-5102-02” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-02</b></li>
     *   <li>Latitude: <b>57</b></li>
     *   <li>Longitude: <b>5</b></li>
     *   <li>Easting: <b>764566.844</b></li>
     *   <li>Northing: <b>3343948.93</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-02 : REVERSE")
    public void GIGS_5102_02() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{764566.844, 3343948.93};
        final double[] destinationPoint = new double[]{57, 5};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5102-03” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-03</b></li>
     *   <li>Latitude: <b>56</b></li>
     *   <li>Longitude: <b>5</b></li>
     *   <li>Easting: <b>768396.683</b></li>
     *   <li>Northing: <b>3230944.812</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-03 : FORWARD")
    public void GIGS_5102_03() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{56, 5};
        final double[] destinationPoint = new double[]{768396.683, 3230944.812};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5102-04” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-04</b></li>
     *   <li>Latitude: <b>55</b></li>
     *   <li>Longitude: <b>5</b></li>
     *   <li>Easting: <b>772213.973</b></li>
     *   <li>Northing: <b>3118310.947</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-04 : REVERSE")
    public void GIGS_5102_04() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{772213.973, 3118310.947};
        final double[] destinationPoint = new double[]{55, 5};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5102-05” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-05</b></li>
     *   <li>Latitude: <b>54</b></li>
     *   <li>Longitude: <b>5</b></li>
     *   <li>Easting: <b>776020.189</b></li>
     *   <li>Northing: <b>3006003.839</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-05 : FORWARD")
    public void GIGS_5102_05() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{54, 5};
        final double[] destinationPoint = new double[]{776020.189, 3006003.839};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5102-06” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-06</b></li>
     *   <li>Latitude: <b>53</b></li>
     *   <li>Longitude: <b>5</b></li>
     *   <li>Easting: <b>779816.748</b></li>
     *   <li>Northing: <b>2893981.68</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-06 : REVERSE")
    public void GIGS_5102_06() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{779816.748, 2893981.68};
        final double[] destinationPoint = new double[]{53, 5};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5102-07” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-07</b></li>
     *   <li>Latitude: <b>51</b></li>
     *   <li>Longitude: <b>4</b></li>
     *   <li>Easting: <b>717027.292</b></li>
     *   <li>Northing: <b>2668695.784</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-07 : FORWARD")
    public void GIGS_5102_07() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{51, 4};
        final double[] destinationPoint = new double[]{717027.292, 2668695.784};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5102-08” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-08</b></li>
     *   <li>Latitude: <b>50</b></li>
     *   <li>Longitude: <b>4</b></li>
     *   <li>Easting: <b>719385.249</b></li>
     *   <li>Northing: <b>2557252.841</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-08 : REVERSE")
    public void GIGS_5102_08() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{719385.249, 2557252.841};
        final double[] destinationPoint = new double[]{50, 4};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5102-09” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-09</b></li>
     *   <li>Latitude: <b>49</b></li>
     *   <li>Longitude: <b>4</b></li>
     *   <li>Easting: <b>721740.43</b></li>
     *   <li>Northing: <b>2445941.161</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-09 : FORWARD")
    public void GIGS_5102_09() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{49, 4};
        final double[] destinationPoint = new double[]{721740.43, 2445941.161};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5102-10” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-10</b></li>
     *   <li>Latitude: <b>46.8</b></li>
     *   <li>Longitude: <b>4</b></li>
     *   <li>Easting: <b>726915.752</b></li>
     *   <li>Northing: <b>2201342.518</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-10 : REVERSE")
    public void GIGS_5102_10() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{726915.752, 2201342.518};
        final double[] destinationPoint = new double[]{46.8, 4};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5102-11” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-11</b></li>
     *   <li>Latitude: <b>53</b></li>
     *   <li>Longitude: <b>3</b></li>
     *   <li>Easting: <b>644764.905</b></li>
     *   <li>Northing: <b>2891124.195</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-11 : FORWARD")
    public void GIGS_5102_11() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{53, 3};
        final double[] destinationPoint = new double[]{644764.905, 2891124.195};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5102-12” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-12</b></li>
     *   <li>Latitude: <b>53</b></li>
     *   <li>Longitude: <b>4</b></li>
     *   <li>Easting: <b>712299.916</b></li>
     *   <li>Northing: <b>2892123.369</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-12 : REVERSE")
    public void GIGS_5102_12() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{712299.916, 2892123.369};
        final double[] destinationPoint = new double[]{53, 4};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5102-13” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-13</b></li>
     *   <li>Latitude: <b>53</b></li>
     *   <li>Longitude: <b>5</b></li>
     *   <li>Easting: <b>779816.748</b></li>
     *   <li>Northing: <b>2893981.68</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-13 : FORWARD")
    public void GIGS_5102_13() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{53, 5};
        final double[] destinationPoint = new double[]{779816.748, 2893981.68};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5102-14” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-14</b></li>
     *   <li>Latitude: <b>53</b></li>
     *   <li>Longitude: <b>6</b></li>
     *   <li>Easting: <b>847304.473</b></li>
     *   <li>Northing: <b>2896698.827</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-14 : REVERSE")
    public void GIGS_5102_14() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{847304.473, 2896698.827};
        final double[] destinationPoint = new double[]{53, 6};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5102-15” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-15</b></li>
     *   <li>Latitude: <b>53</b></li>
     *   <li>Longitude: <b>7</b></li>
     *   <li>Easting: <b>914752.168</b></li>
     *   <li>Northing: <b>2900274.371</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-15 : FORWARD")
    public void GIGS_5102_15() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{53, 7};
        final double[] destinationPoint = new double[]{914752.168, 2900274.371};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5102-16” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-16</b></li>
     *   <li>Latitude: <b>53</b></li>
     *   <li>Longitude: <b>8</b></li>
     *   <li>Easting: <b>982148.913</b></li>
     *   <li>Northing: <b>2904707.734</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-16 : REVERSE")
    public void GIGS_5102_16() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{982148.913, 2904707.734};
        final double[] destinationPoint = new double[]{53, 8};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5102-17” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-17</b></li>
     *   <li>Latitude: <b>53</b></li>
     *   <li>Longitude: <b>9</b></li>
     *   <li>Easting: <b>1049483.8</b></li>
     *   <li>Northing: <b>2909998.196</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-17 : FORWARD")
    public void GIGS_5102_17() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{53, 9};
        final double[] destinationPoint = new double[]{1049483.8, 2909998.196};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5102-18” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-18</b></li>
     *   <li>Latitude: <b>53</b></li>
     *   <li>Longitude: <b>10</b></li>
     *   <li>Easting: <b>1116745.929</b></li>
     *   <li>Northing: <b>2916144.902</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-18 : REVERSE")
    public void GIGS_5102_18() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{1116745.929, 2916144.902};
        final double[] destinationPoint = new double[]{53, 10};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5102-19” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-19</b></li>
     *   <li>Latitude: <b>53</b></li>
     *   <li>Longitude: <b>11</b></li>
     *   <li>Easting: <b>1183924.412</b></li>
     *   <li>Northing: <b>2923146.858</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-19 : FORWARD")
    public void GIGS_5102_19() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{53, 11};
        final double[] destinationPoint = new double[]{1183924.412, 2923146.858};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }
}
