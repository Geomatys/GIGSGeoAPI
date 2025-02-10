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
 * Verifies the software’s capabilities to perform conversions for the Transverse Mercator (South Oriented) map projection.
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
 *   <td><a href="https://github.com/IOGP-GIGS/GIGSTestDataset/blob/main/GIGSTestDatasetFiles/GIGS%205100%20Conversion%20test%20data/ASCII/GIGS_conv_5113_TMSO_input.txt">{@code GIGS_conv_5113_TMSO_input.txt}</a>
 * </tr><tr>
 *   <th>Tested API:</th>
 *   <td>{@link CoordinateOperationFactory#createOperation(CoordinateReferenceSystem, CoordinateReferenceSystem)}.</td>
 * </tr><tr>
 *   <th>Expected result:</th>
 *   <td>Results for the forward and reverse calculations should agree to within 0.05m (50mm) or 0.0000006°
 *   of the Test Data. See file GIGS_conv_5113_TMSO_output.
 *   For the round trip calculation the initial calculated coordinates of the point should change by less
 *   than 0.003m (3mm) or 0.0000003° (before 1000 iterations).
 *   Test result will be pass or fail. If fail, details of failure should be reported.</td>
 * </tr></table>
 *
 *
 * <h2>Usage example</h2>
 * In order to specify their factories and run the tests in a JUnit framework, implementers can
 * define a subclass in their own test suite as in the example below:
 * <p>
 * {@snippet lang = "java":
 * public class MyTest extends Test5113 {
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
@DisplayName("Transverse Mercator (South Oriented)")
public class Test5113 extends Series5100 {

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
    public Test5113(final Factories factories) throws FactoryException {
        super(factories, 0.03, 0.0000003, 0.006, 0.00000006);
    }

    /**
     * Method used in class instantiation to initialise the geographic CRS and the projected CRS
     * used in this test class.
     * <p>Geographic CRS : GIGS CRS Code 64010; GIGS geogCRS G; Hartebeesthoek94; decimal degree; EPSG CRS code 4148
     * <p>ProjectedCRS : GIGS CRS Code 62017; GIGS projCRS G10; Hartebeesthoek94 / Lo21; metre; EPSG CRS code 2049
     *
     * @throws FactoryException if an error occurred while creating a CRS.
     */
    @Override
    protected void createCRSs() throws FactoryException {
        createGeogCRS(Test3205::GIGS_64010);
        createProjCRS(Test3207::GIGS_62017);
    }

    /**
     * Tests “GIGS-5513-01” for TMSO calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5513-01</b></li>
     *   <li>Latitude: <b>0</b></li>
     *   <li>Longitude: <b>22.5</b></li>
     *   <li>Easting: <b>-166998.442</b></li>
     *   <li>Northing: <b>0</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5513-01 : FORWARD")
    public void GIGS_5513_01() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{0, 22.5};
        final double[] destinationPoint = new double[]{-166998.442, 0};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5513-02” for TMSO calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5513-02</b></li>
     *   <li>Latitude: <b>-25</b></li>
     *   <li>Longitude: <b>21.5</b></li>
     *   <li>Easting: <b>-50475.46</b></li>
     *   <li>Northing: <b>2766147.248</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5513-02 : REVERSE")
    public void GIGS_5513_02() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-50475.46, 2766147.248};
        final double[] destinationPoint = new double[]{-25, 21.5};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5513-03” for TMSO calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5513-03</b></li>
     *   <li>Latitude: <b>-30</b></li>
     *   <li>Longitude: <b>20.5</b></li>
     *   <li>Easting: <b>48243.449</b></li>
     *   <li>Northing: <b>3320218.65</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5513-03 : FORWARD")
    public void GIGS_5513_03() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-30, 20.5};
        final double[] destinationPoint = new double[]{48243.449, 3320218.65};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5513-04” for TMSO calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5513-04</b></li>
     *   <li>Latitude: <b>-35</b></li>
     *   <li>Longitude: <b>19.5</b></li>
     *   <li>Easting: <b>136937.651</b></li>
     *   <li>Northing: <b>3875621.182</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5513-04 : REVERSE")
    public void GIGS_5513_04() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{136937.651, 3875621.182};
        final double[] destinationPoint = new double[]{-35, 19.5};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5513-05” for TMSO calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5513-05</b></li>
     *   <li>Latitude: <b>-35</b></li>
     *   <li>Longitude: <b>19.5</b></li>
     *   <li>Easting: <b>136937.651</b></li>
     *   <li>Northing: <b>3875621.182</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: Round Trip calculation point.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5513-05 : FORWARD")
    public void GIGS_5513_05() throws TransformException {
        isRTC = true;
        isForward = true;
        final double[] originPoint = new double[]{-35, 19.5};
        final double[] destinationPoint = new double[]{136937.651, 3875621.182};
        convertAndVerifyRoundTripPoint(originPoint, destinationPoint);
    }
}
