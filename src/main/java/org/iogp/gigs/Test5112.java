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
 * Verifies the software’s capabilities to perform conversions for the Mercator (variant B) map projection.
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
 *   <td><a href="https://github.com/IOGP-GIGS/GIGSTestDataset/blob/main/GIGSTestDatasetFiles/GIGS%205100%20Conversion%20test%20data/ASCII/GIGS_conv_5112_MercB_input.txt">{@code GIGS_conv_5112_MercB_input.txt}</a>
 * </tr><tr>
 *   <th>Tested API:</th>
 *   <td>{@link CoordinateOperationFactory#createOperation(CoordinateReferenceSystem, CoordinateReferenceSystem)}.</td>
 * </tr><tr>
 *   <th>Expected result:</th>
 *   <td>Results for the forward and reverse calculations should agree to within 0.05m (50mm) or 0.0000006°
 *   of the Test Data. See file GIGS_conv_5112_MercB_output.
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
 * public class MyTest extends Test5112 {
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
@DisplayName("Mercator (variant B)")
public class Test5112 extends Series5100 {

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
    public Test5112(final Factories factories) throws FactoryException {
        super(factories, 0.05, 0.0000006, 0.006, 0.00000006);
    }

    /**
     * Method used in class instantiation to initialise the geographic CRS and the projected CRS
     * used in this test class.
     * <p>Geographic CRS : GIGS CRS Code 64017; GIGS geogCRS Y; Pulkovo 1942; decimal degree; EPSG CRS code 4284
     * <p>ProjectedCRS : GIGS CRS Code 62034; GIGS projCRS Y24; Pulkovo 1942 / Caspian Sea Mercator; metre; EPSG CRS code 3388
     *
     * @throws FactoryException if an error occurred while creating a CRS.
     */
    @Override
    protected void createCRSs() throws FactoryException {
        createGeogCRS(Test3205::GIGS_64017);
        createProjCRS(Test3207::GIGS_62034);
    }

    /**
     * Tests “GIGS-5112-01” for MercB calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5112-01</b></li>
     *   <li>Latitude: <b>42</b></li>
     *   <li>Longitude: <b>51</b></li>
     *   <li>Easting: <b>3819897.852</b></li>
     *   <li>Northing: <b>0</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5112-01 : REVERSE")
    public void GIGS_5112_01() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{3819897.852, 0};
        final double[] destinationPoint = new double[]{42, 51};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5112-02” for MercB calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5112-02</b></li>
     *   <li>Latitude: <b>0</b></li>
     *   <li>Longitude: <b>51</b></li>
     *   <li>Easting: <b>0</b></li>
     *   <li>Northing: <b>0</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5112-02 : FORWARD")
    public void GIGS_5112_02() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{0, 51};
        final double[] destinationPoint = new double[]{0, 0};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5112-03” for MercB calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5112-03</b></li>
     *   <li>Latitude: <b>0</b></li>
     *   <li>Longitude: <b>57</b></li>
     *   <li>Easting: <b>0</b></li>
     *   <li>Northing: <b>497112.88</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5112-03 : REVERSE")
    public void GIGS_5112_03() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{0, 497112.88};
        final double[] destinationPoint = new double[]{0, 57};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5112-04” for MercB calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5112-04</b></li>
     *   <li>Latitude: <b>20.5</b></li>
     *   <li>Longitude: <b>54</b></li>
     *   <li>Easting: <b>1724781.5</b></li>
     *   <li>Northing: <b>248556.44</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5112-04 : FORWARD")
    public void GIGS_5112_04() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{20.5, 54};
        final double[] destinationPoint = new double[]{1724781.5, 248556.44};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5112-05” for MercB calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5112-05</b></li>
     *   <li>Latitude: <b>-41</b></li>
     *   <li>Longitude: <b>67</b></li>
     *   <li>Easting: <b>-3709687.255</b></li>
     *   <li>Northing: <b>1325634.346</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * Remarks: Round Trip calculation point.
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5112-05 : FORWARD")
    public void GIGS_5112_05() throws TransformException {
        isRTC = true;
        isForward = true;
        final double[] originPoint = new double[]{-41, 67};
        final double[] destinationPoint = new double[]{-3709687.255, 1325634.346};
        convertAndVerifyRoundTripPoint(originPoint, destinationPoint);
    }
}
