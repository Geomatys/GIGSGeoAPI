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
 *   <td><a href="https://github.com/IOGP-GIGS/GIGSTestDataset/blob/main/GIGSTestDatasetFiles/GIGS%205100%20Conversion%20test%20data/ASCII/GIGS_conv_5101_TM_input_part3.txt">{@code GIGS_conv_5101_TM_input_part3.txt}</a>
 * </tr><tr>
 *   <th>Tested API:</th>
 *   <td>{@link CoordinateOperationFactory#createOperation(CoordinateReferenceSystem, CoordinateReferenceSystem)}.</td>
 * </tr><tr>
 *   <th>Expected result:</th>
 *   <td>Results for the forward and reverse calculations should agree to within 0.03m (30mm) or
 *   0.0000003° of the Test Data. See file GIGS_conv_5101_TM_output_part3_JHS.
 *   For the round trip calculation the initial calculated coordinates of the point should change by less
 *   than 0.006m (6mm) or 0.00000006° (before 1000 iterations).
 *   Test result will be pass or fail. If fail, details of failure should be reported.</td>
 * </tr></table>
 *
 *efine a subclass in their own test suite as in the example below:
 *
 * {@snippet lang = "java":
 * public class MyTest extends Test51013 {
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
public class Test51013 extends Series5100 {
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
    public Test51013(final Factories factories) throws FactoryException {
        super(factories, 0.03, 0.0000003, 0.006, 0.00000006);
    }

    /**
     * Method used in class instantiation to initialise the geographic CRS and the projected CRS
     * used in this test class.
     * <p>Geographic CRS : GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283
     * <p>ProjectedCRS : GIGS CRS Code 62014; GIGS projCRS F7; GDA94 / MGA zone 54; metre; EPSG CRS code 28354
     *
     * @throws FactoryException if an error occurred while creating a CRS.
     */
    @Override
    protected void createCRSs() throws FactoryException {
        createGeogCRS(Test3205::GIGS_64009);
        createProjCRS(Test3207::GIGS_62014);
    }

    /**
     * Tests “GIGS-5101-100” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-100</b></li>
     *   <li>Latitude: <b>-60</b></li>
     *   <li>Longitude: <b>144</b></li>
     *   <li>Easting: <b>667294.821</b></li>
     *   <li>Northing: <b>3344794.516</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-100 : FORWARD")
    public void GIGS_5101_100() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-60, 144};
        final double[] destinationPoint = new double[]{667294.821, 3344794.516};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-101” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-101</b></li>
     *   <li>Latitude: <b>-60</b></li>
     *   <li>Longitude: <b>145</b></li>
     *   <li>Easting: <b>723020.074</b></li>
     *   <li>Northing: <b>3341842.798</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-101 : REVERSE")
    public void GIGS_5101_101() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{723020.074, 3341842.798};
        final double[] destinationPoint = new double[]{-60, 145};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-102” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-102</b></li>
     *   <li>Latitude: <b>-60</b></li>
     *   <li>Longitude: <b>146</b></li>
     *   <li>Easting: <b>778711.23</b></li>
     *   <li>Northing: <b>3338046.96</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-102 : FORWARD")
    public void GIGS_5101_102() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-60, 146};
        final double[] destinationPoint = new double[]{778711.23, 3338046.96};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-103” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-103</b></li>
     *   <li>Latitude: <b>-60</b></li>
     *   <li>Longitude: <b>147</b></li>
     *   <li>Easting: <b>834359.668</b></li>
     *   <li>Northing: <b>3333406.428</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-103 : REVERSE")
    public void GIGS_5101_103() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{834359.668, 3333406.428};
        final double[] destinationPoint = new double[]{-60, 147};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-104” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-104</b></li>
     *   <li>Latitude: <b>-60</b></li>
     *   <li>Longitude: <b>148</b></li>
     *   <li>Easting: <b>889956.701</b></li>
     *   <li>Northing: <b>3327920.506</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-104 : FORWARD")
    public void GIGS_5101_104() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-60, 148};
        final double[] destinationPoint = new double[]{889956.701, 3327920.506};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-105” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-105</b></li>
     *   <li>Latitude: <b>-60</b></li>
     *   <li>Longitude: <b>149</b></li>
     *   <li>Easting: <b>945493.565</b></li>
     *   <li>Northing: <b>3321588.377</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-105 : REVERSE")
    public void GIGS_5101_105() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{945493.565, 3321588.377};
        final double[] destinationPoint = new double[]{-60, 149};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-83” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-83</b></li>
     *   <li>Latitude: <b>80</b></li>
     *   <li>Longitude: <b>146</b></li>
     *   <li>Easting: <b>596813.055</b></li>
     *   <li>Northing: <b>18885748.708</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-83 : REVERSE")
    public void GIGS_5101_83() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{596813.055, 18885748.708};
        final double[] destinationPoint = new double[]{80, 146};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-84” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-84</b></li>
     *   <li>Latitude: <b>60</b></li>
     *   <li>Longitude: <b>146</b></li>
     *   <li>Easting: <b>778711.23</b></li>
     *   <li>Northing: <b>16661953.04</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-84 : FORWARD")
    public void GIGS_5101_84() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{60, 146};
        final double[] destinationPoint = new double[]{778711.23, 16661953.04};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-85” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-85</b></li>
     *   <li>Latitude: <b>40</b></li>
     *   <li>Longitude: <b>146</b></li>
     *   <li>Easting: <b>926893.302</b></li>
     *   <li>Northing: <b>14439746.917</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-85 : REVERSE")
    public void GIGS_5101_85() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{926893.302, 14439746.917};
        final double[] destinationPoint = new double[]{40, 146};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-86” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-86</b></li>
     *   <li>Latitude: <b>20</b></li>
     *   <li>Longitude: <b>146</b></li>
     *   <li>Easting: <b>1023538.687</b></li>
     *   <li>Northing: <b>12219308.24</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-86 : FORWARD")
    public void GIGS_5101_86() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{20, 146};
        final double[] destinationPoint = new double[]{1023538.687, 12219308.24};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-87” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-87</b></li>
     *   <li>Latitude: <b>0</b></li>
     *   <li>Longitude: <b>146</b></li>
     *   <li>Easting: <b>1057087.12</b></li>
     *   <li>Northing: <b>10000000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-87 : REVERSE")
    public void GIGS_5101_87() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{1057087.12, 10000000};
        final double[] destinationPoint = new double[]{0, 146};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-88” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-88</b></li>
     *   <li>Latitude: <b>-20</b></li>
     *   <li>Longitude: <b>146</b></li>
     *   <li>Easting: <b>1023538.687</b></li>
     *   <li>Northing: <b>7780691.762</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-88 : FORWARD")
    public void GIGS_5101_88() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-20, 146};
        final double[] destinationPoint = new double[]{1023538.687, 7780691.762};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-89” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-89</b></li>
     *   <li>Latitude: <b>-40</b></li>
     *   <li>Longitude: <b>146</b></li>
     *   <li>Easting: <b>926893.302</b></li>
     *   <li>Northing: <b>5560253.083</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-89 : FORWARD")
    public void GIGS_5101_89() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-40, 146};
        final double[] destinationPoint = new double[]{926893.302, 5560253.083};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-90” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-90</b></li>
     *   <li>Latitude: <b>-60</b></li>
     *   <li>Longitude: <b>146</b></li>
     *   <li>Easting: <b>778711.23</b></li>
     *   <li>Northing: <b>3338046.96</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-90 : REVERSE")
    public void GIGS_5101_90() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{778711.23, 3338046.96};
        final double[] destinationPoint = new double[]{-60, 146};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-91” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-91</b></li>
     *   <li>Latitude: <b>-80</b></li>
     *   <li>Longitude: <b>146</b></li>
     *   <li>Easting: <b>596813.055</b></li>
     *   <li>Northing: <b>1114251.292</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-91 : FORWARD")
    public void GIGS_5101_91() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-80, 146};
        final double[] destinationPoint = new double[]{596813.055, 1114251.292};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-92” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-92</b></li>
     *   <li>Latitude: <b>-60</b></li>
     *   <li>Longitude: <b>136</b></li>
     *   <li>Easting: <b>221288.77</b></li>
     *   <li>Northing: <b>3338046.96</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-92 : FORWARD")
    public void GIGS_5101_92() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-60, 136};
        final double[] destinationPoint = new double[]{221288.77, 3338046.96};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-93” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-93</b></li>
     *   <li>Latitude: <b>-60</b></li>
     *   <li>Longitude: <b>137</b></li>
     *   <li>Easting: <b>276979.926</b></li>
     *   <li>Northing: <b>3341842.798</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-93 : REVERSE")
    public void GIGS_5101_93() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{276979.926, 3341842.798};
        final double[] destinationPoint = new double[]{-60, 137};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-94” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-94</b></li>
     *   <li>Latitude: <b>-60</b></li>
     *   <li>Longitude: <b>138</b></li>
     *   <li>Easting: <b>332705.179</b></li>
     *   <li>Northing: <b>3344794.516</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-94 : FORWARD")
    public void GIGS_5101_94() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-60, 138};
        final double[] destinationPoint = new double[]{332705.179, 3344794.516};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-95” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-95</b></li>
     *   <li>Latitude: <b>-60</b></li>
     *   <li>Longitude: <b>139</b></li>
     *   <li>Easting: <b>388455.958</b></li>
     *   <li>Northing: <b>3346902.565</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-95 : REVERSE")
    public void GIGS_5101_95() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{388455.958, 3346902.565};
        final double[] destinationPoint = new double[]{-60, 139};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-96” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-96</b></li>
     *   <li>Latitude: <b>-60</b></li>
     *   <li>Longitude: <b>140</b></li>
     *   <li>Easting: <b>444223.733</b></li>
     *   <li>Northing: <b>3348167.265</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-96 : FORWARD")
    public void GIGS_5101_96() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-60, 140};
        final double[] destinationPoint = new double[]{444223.733, 3348167.265};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-97” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-97</b></li>
     *   <li>Latitude: <b>-60</b></li>
     *   <li>Longitude: <b>141</b></li>
     *   <li>Easting: <b>500000</b></li>
     *   <li>Northing: <b>3348588.81</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-97 : REVERSE")
    public void GIGS_5101_97() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{500000, 3348588.81};
        final double[] destinationPoint = new double[]{-60, 141};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-98” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-98</b></li>
     *   <li>Latitude: <b>-60</b></li>
     *   <li>Longitude: <b>142</b></li>
     *   <li>Easting: <b>555776.267</b></li>
     *   <li>Northing: <b>3348167.265</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-98 : FORWARD")
    public void GIGS_5101_98() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-60, 142};
        final double[] destinationPoint = new double[]{555776.267, 3348167.265};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5101-99” for TM calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5101-99</b></li>
     *   <li>Latitude: <b>-60</b></li>
     *   <li>Longitude: <b>143</b></li>
     *   <li>Easting: <b>611544.042</b></li>
     *   <li>Northing: <b>3346902.565</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5101-99 : REVERSE")
    public void GIGS_5101_99() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{611544.042, 3346902.565};
        final double[] destinationPoint = new double[]{-60, 143};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }
}
