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
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;


/**
 * Verifies the software’s capabilities to perform conversions for the Transverse Mercator map projection.
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
 *   <td><a href="https://github.com/IOGP-GIGS/GIGSTestDataset/blob/main/GIGSTestDatasetFiles/GIGS%205100%20Conversion%20test%20data/ASCII/GIGS_conv_5101_TM_input_part2.txt">{@code GIGS_conv_5101_TM_input_part2.txt}</a>
 * </tr><tr>
 *   <th>Tested API:</th>
 *   <td>{@link CoordinateOperationFactory#createOperation(CoordinateReferenceSystem, CoordinateReferenceSystem)}.</td>
 * </tr><tr>
 *   <th>Expected result:</th>
 *   <td>Results for the forward and reverse calculations should agree to within 0.03m (30mm) or
 *   0.0000003° of the Test Data. See file GIGS_conv_5101_TM_output_part2_JHS.
 *   For the round trip calculation the initial calculated coordinates of the point should change by less
 *   than 0.006m (6mm) or 0.00000006° (before 1000 iterations).
 *   Test result will be pass or fail. If fail, details of failure should be reported.</td>
 * </tr></table>
 *
 *efine a subclass in their own test suite as in the example below:
 *
 * {@snippet lang = "java":
 * public class MyTest extends Test51012 {
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
@DisplayName("Transverse Mercator conversion")
public class Test51012 extends Series5100 {
    /**
     * Creates a new test using the given factories.
     * The factories needed by this class are {@link CoordinateOperationFactory} and {@link org.opengis.referencing.crs.CRSAuthorityFactory}.
     * If a requested factory is {@code null}, then the tests which depend on it will be skipped.
     *
     * <h4>Authority factory usage</h4>
     * The authority factory is used only to fetch the Coordinates Reference Systems by EPSG codes
     * instead of being built by user.
     *
     * @param factories  factories for creating the instances to test.
     *
     * @throws FactoryException if an error occurred while creating the CRSs.
     */
    public Test51012(final Factories factories) throws FactoryException {
        super(factories, 0.03, 0.0000003, 0.006, 0.00000006);
    }

    /**
     * Method used in class instantiation to initialise the geographic CRS and the projected CRS
     * used in this test class.
     * <p>Geographic CRS : GIGS CRS Code 64003; GIGS geogCRS A; WGS 84; decimal degree; EPSG CRS code 4326
     * <p>ProjectedCRS : GIGS CRS Code 62001; GIGS projCRS A1; WGS 84 / UTM zone 31N; metre; EPSG CRS code 32631
     *
     * @throws FactoryException if an error occurred while creating a CRS.
     */
    @Override
    protected void createCRSs() throws FactoryException {
        createGeogCRS(Test3205::GIGS_64003);
        createProjCRS(Test3207::GIGS_62001);
    }

    /**
     * Tests “GIGS-5101-60” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-60</b></li>
     *   <li>Latitude: <b>80</b></li>
     *   <li>Longitude: <b>-2</b></li>
     *   <li>Easting: <b>403186.945</b></li>
     *   <li>Northing: <b>8885748.708</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-60 : REVERSE")
    public void GIGS_5101_60() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{403186.945, 8885748.708};
        final double[] destinationPoint = new double[]{80, -2};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-61” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-61</b></li>
     *   <li>Latitude: <b>60</b></li>
     *   <li>Longitude: <b>-2</b></li>
     *   <li>Easting: <b>221288.77</b></li>
     *   <li>Northing: <b>6661953.041</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-61 : FORWARD")
    public void GIGS_5101_61() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{60, -2};
        final double[] destinationPoint = new double[]{221288.77, 6661953.041};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-62” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-62</b></li>
     *   <li>Latitude: <b>40</b></li>
     *   <li>Longitude: <b>-2</b></li>
     *   <li>Easting: <b>73106.698</b></li>
     *   <li>Northing: <b>4439746.917</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-62 : REVERSE")
    public void GIGS_5101_62() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{73106.698, 4439746.917};
        final double[] destinationPoint = new double[]{40, -2};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-63” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-63</b></li>
     *   <li>Latitude: <b>20</b></li>
     *   <li>Longitude: <b>-2</b></li>
     *   <li>Easting: <b>-23538.687</b></li>
     *   <li>Northing: <b>2219308.238</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-63 : FORWARD")
    public void GIGS_5101_63() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{20, -2};
        final double[] destinationPoint = new double[]{-23538.687, 2219308.238};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-64” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-64</b></li>
     *   <li>Latitude: <b>0</b></li>
     *   <li>Longitude: <b>-2</b></li>
     *   <li>Easting: <b>-57087.12</b></li>
     *   <li>Northing: <b>0</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-64 : FORWARD")
    public void GIGS_5101_64() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{0, -2};
        final double[] destinationPoint = new double[]{-57087.12, 0};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-65” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-65</b></li>
     *   <li>Latitude: <b>-20</b></li>
     *   <li>Longitude: <b>-2</b></li>
     *   <li>Easting: <b>-23538.687</b></li>
     *   <li>Northing: <b>-2219308.238</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-65 : REVERSE")
    public void GIGS_5101_65() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-23538.687, -2219308.238};
        final double[] destinationPoint = new double[]{-20, -2};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-66” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-66</b></li>
     *   <li>Latitude: <b>-40</b></li>
     *   <li>Longitude: <b>-2</b></li>
     *   <li>Easting: <b>73106.698</b></li>
     *   <li>Northing: <b>-4439746.917</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-66 : FORWARD")
    public void GIGS_5101_66() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-40, -2};
        final double[] destinationPoint = new double[]{73106.698, -4439746.917};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-67” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-67</b></li>
     *   <li>Latitude: <b>-60</b></li>
     *   <li>Longitude: <b>-2</b></li>
     *   <li>Easting: <b>221288.77</b></li>
     *   <li>Northing: <b>-6661953.041</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-67 : REVERSE")
    public void GIGS_5101_67() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{221288.77, -6661953.041};
        final double[] destinationPoint = new double[]{-60, -2};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-68” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-68</b></li>
     *   <li>Latitude: <b>-80</b></li>
     *   <li>Longitude: <b>-2</b></li>
     *   <li>Easting: <b>403186.945</b></li>
     *   <li>Northing: <b>-8885748.708</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-68 : FORWARD")
    public void GIGS_5101_68() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-80, -2};
        final double[] destinationPoint = new double[]{403186.945, -8885748.708};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-69” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-69</b></li>
     *   <li>Latitude: <b>60</b></li>
     *   <li>Longitude: <b>-5</b></li>
     *   <li>Easting: <b>54506.435</b></li>
     *   <li>Northing: <b>6678411.623</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-69 : FORWARD")
    public void GIGS_5101_69() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{60, -5};
        final double[] destinationPoint = new double[]{54506.435, 6678411.623};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-70” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-70</b></li>
     *   <li>Latitude: <b>60</b></li>
     *   <li>Longitude: <b>-4</b></li>
     *   <li>Easting: <b>110043.299</b></li>
     *   <li>Northing: <b>6672079.494</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-70 : REVERSE")
    public void GIGS_5101_70() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{110043.299, 6672079.494};
        final double[] destinationPoint = new double[]{60, -4};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-71” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-71</b></li>
     *   <li>Latitude: <b>60</b></li>
     *   <li>Longitude: <b>-3</b></li>
     *   <li>Easting: <b>165640.332</b></li>
     *   <li>Northing: <b>6666593.572</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-71 : FORWARD")
    public void GIGS_5101_71() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{60, -3};
        final double[] destinationPoint = new double[]{165640.332, 6666593.572};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-72” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-72</b></li>
     *   <li>Latitude: <b>60</b></li>
     *   <li>Longitude: <b>-2</b></li>
     *   <li>Easting: <b>221288.77</b></li>
     *   <li>Northing: <b>6661953.041</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-72 : REVERSE")
    public void GIGS_5101_72() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{221288.77, 6661953.041};
        final double[] destinationPoint = new double[]{60, -2};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-73” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-73</b></li>
     *   <li>Latitude: <b>60</b></li>
     *   <li>Longitude: <b>-1</b></li>
     *   <li>Easting: <b>276979.926</b></li>
     *   <li>Northing: <b>6658157.202</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-73 : FORWARD")
    public void GIGS_5101_73() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{60, -1};
        final double[] destinationPoint = new double[]{276979.926, 6658157.202};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-74” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-74</b></li>
     *   <li>Latitude: <b>60</b></li>
     *   <li>Longitude: <b>0</b></li>
     *   <li>Easting: <b>332705.179</b></li>
     *   <li>Northing: <b>6655205.484</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-74 : REVERSE")
    public void GIGS_5101_74() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{332705.179, 6655205.484};
        final double[] destinationPoint = new double[]{60, 0};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-75” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-75</b></li>
     *   <li>Latitude: <b>60</b></li>
     *   <li>Longitude: <b>1</b></li>
     *   <li>Easting: <b>388455.958</b></li>
     *   <li>Northing: <b>6653097.435</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-75 : FORWARD")
    public void GIGS_5101_75() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{60, 1};
        final double[] destinationPoint = new double[]{388455.958, 6653097.435};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-76” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-76</b></li>
     *   <li>Latitude: <b>60</b></li>
     *   <li>Longitude: <b>2</b></li>
     *   <li>Easting: <b>444223.733</b></li>
     *   <li>Northing: <b>6651832.735</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-76 : REVERSE")
    public void GIGS_5101_76() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{444223.733, 6651832.735};
        final double[] destinationPoint = new double[]{60, 2};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-77” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-77</b></li>
     *   <li>Latitude: <b>60</b></li>
     *   <li>Longitude: <b>3</b></li>
     *   <li>Easting: <b>500000</b></li>
     *   <li>Northing: <b>6651411.19</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-77 : FORWARD")
    public void GIGS_5101_77() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{60, 3};
        final double[] destinationPoint = new double[]{500000, 6651411.19};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-78” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-78</b></li>
     *   <li>Latitude: <b>60</b></li>
     *   <li>Longitude: <b>4</b></li>
     *   <li>Easting: <b>555776.267</b></li>
     *   <li>Northing: <b>6651832.735</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-78 : REVERSE")
    public void GIGS_5101_78() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{555776.267, 6651832.735};
        final double[] destinationPoint = new double[]{60, 4};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-79” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-79</b></li>
     *   <li>Latitude: <b>60</b></li>
     *   <li>Longitude: <b>5</b></li>
     *   <li>Easting: <b>611544.042</b></li>
     *   <li>Northing: <b>6653097.435</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-79 : FORWARD")
    public void GIGS_5101_79() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{60, 5};
        final double[] destinationPoint = new double[]{611544.042, 6653097.435};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-80” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-80</b></li>
     *   <li>Latitude: <b>60</b></li>
     *   <li>Longitude: <b>6</b></li>
     *   <li>Easting: <b>667294.821</b></li>
     *   <li>Northing: <b>6655205.484</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-80 : REVERSE")
    public void GIGS_5101_80() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{667294.821, 6655205.484};
        final double[] destinationPoint = new double[]{60, 6};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-81” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-81</b></li>
     *   <li>Latitude: <b>60</b></li>
     *   <li>Longitude: <b>7</b></li>
     *   <li>Easting: <b>723020.074</b></li>
     *   <li>Northing: <b>6658157.202</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-81 : FORWARD")
    public void GIGS_5101_81() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{60, 7};
        final double[] destinationPoint = new double[]{723020.074, 6658157.202};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-82” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-82</b></li>
     *   <li>Latitude: <b>60</b></li>
     *   <li>Longitude: <b>8</b></li>
     *   <li>Easting: <b>778711.23</b></li>
     *   <li>Northing: <b>6661953.041</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-82 : REVERSE")
    public void GIGS_5101_82() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{778711.23, 6661953.041};
        final double[] destinationPoint = new double[]{60, 8};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }
}
