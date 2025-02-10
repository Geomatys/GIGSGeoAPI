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
import org.opengis.referencing.operation.*;
import org.opengis.util.FactoryException;


/**
 * Verifies the software’s capabilities to perform conversions for the Mercator (variant A) map projection.
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
 *   <td><a href="https://github.com/IOGP-GIGS/GIGSTestDataset/blob/main/GIGSTestDatasetFiles/GIGS%205100%20Conversion%20test%20data/ASCII/GIGS_conv_5111_MercA_input_part2.txt">{@code GIGS_conv_5111_MercA_input_part2.txt}</a>
 * </tr><tr>
 *   <th>Tested API:</th>
 *   <td>{@link CoordinateOperationFactory#createOperation(CoordinateReferenceSystem, CoordinateReferenceSystem)}.</td>
 * </tr><tr>
 *   <th>Expected result:</th>
 *   <td>Results for the forward and reverse calculations should agree to within 0.05m (50mm) or 0.0000006°
 *   of the Test Data. See file GIGS_conv_5111_MercA_output_part2.
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
 * public class MyTest extends Test51112 {
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
@DisplayName("Mercator (variant A)")
public class Test51112 extends Series5100 {

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
    public Test51112(final Factories factories) throws FactoryException {
        super(factories, 0.05, 0.0000006, 0.006, 0.00000006);
    }

    /**
     * Method used in class instantiation to initialise the geographic CRS and the projected CRS
     * used in this test class.
     * <p>Geographic CRS : GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813
     * <p>ProjectedCRS : GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330
     *
     * @throws FactoryException if an error occurred while creating a CRS.
     */
    @Override
    protected void createCRSs() throws FactoryException {
        createGeogCRS(Test3205::GIGS_64007);
        createProjCRS(Test3207::GIGS_62012);
    }


    /**
     * Convert the point to the output CRS.
     *
     * @param originPoint the point to convert to the destination CRS.
     * @return the result point from the conversion/transformation
     *
     * @throws TransformException if an error occurred while converting the given point.
     */
    double[] convertPoint(double[] originPoint) throws TransformException {
        final CoordinateOperation distop = isForward ? forwardCoOp : reverseCoOp;

        final MathTransform distTrs = distop.getMathTransform();
        final double[] distRes = new double[distTrs.getTargetDimensions()];
        distTrs.transform(originPoint, 0, distRes, 0, 1);

        if (!isForward && distRes[1] > 180) {
            distRes[1] = distRes[1] - 360;
        }
        return distRes;
    }

    /**
     * Tests “GIGS-5111-36” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-36</b></li>
     *   <li>Latitude: <b>77.6534822</b></li>
     *   <li>Longitude: <b>-6.7200711</b></li>
     *   <li>Easting: <b>2800000</b></li>
     *   <li>Northing: <b>15000000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-36 : FORWARD")
    public void GIGS_5111_36() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{77.6534822, -6.7200711};
        final double[] destinationPoint = new double[]{2800000, 15000000};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-37” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-37</b></li>
     *   <li>Latitude: <b>73.1442856</b></li>
     *   <li>Longitude: <b>-6.7200711</b></li>
     *   <li>Easting: <b>2800000</b></li>
     *   <li>Northing: <b>13000000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-37 : REVERSE")
    public void GIGS_5111_37() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{2800000, 13000000};
        final double[] destinationPoint = new double[]{73.1442856, -6.7200711};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-38” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-38</b></li>
     *   <li>Latitude: <b>67.0518325</b></li>
     *   <li>Longitude: <b>-6.7200711</b></li>
     *   <li>Easting: <b>2800000</b></li>
     *   <li>Northing: <b>11000000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-38 : FORWARD")
    public void GIGS_5111_38() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{67.0518325, -6.7200711};
        final double[] destinationPoint = new double[]{2800000, 11000000};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-39” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-39</b></li>
     *   <li>Latitude: <b>58.9140458</b></li>
     *   <li>Longitude: <b>-6.7200711</b></li>
     *   <li>Easting: <b>2800000</b></li>
     *   <li>Northing: <b>9000000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-39 : REVERSE")
    public void GIGS_5111_39() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{2800000, 9000000};
        final double[] destinationPoint = new double[]{58.9140458, -6.7200711};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-40” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-40</b></li>
     *   <li>Latitude: <b>48.2638981</b></li>
     *   <li>Longitude: <b>-6.7200711</b></li>
     *   <li>Easting: <b>2800000</b></li>
     *   <li>Northing: <b>7000000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-40 : FORWARD")
    public void GIGS_5111_40() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{48.2638981, -6.7200711};
        final double[] destinationPoint = new double[]{2800000, 7000000};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-41” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-41</b></li>
     *   <li>Latitude: <b>34.8029044</b></li>
     *   <li>Longitude: <b>-6.7200711</b></li>
     *   <li>Easting: <b>2800000</b></li>
     *   <li>Northing: <b>5000000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-41 : REVERSE")
    public void GIGS_5111_41() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{2800000, 5000000};
        final double[] destinationPoint = new double[]{34.8029044, -6.7200711};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-42” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-42</b></li>
     *   <li>Latitude: <b>18.7048581</b></li>
     *   <li>Longitude: <b>-6.7200711</b></li>
     *   <li>Easting: <b>2800000</b></li>
     *   <li>Northing: <b>3000000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-42 : FORWARD")
    public void GIGS_5111_42() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{18.7048581, -6.7200711};
        final double[] destinationPoint = new double[]{2800000, 3000000};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-43” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-43</b></li>
     *   <li>Latitude: <b>0.9071392</b></li>
     *   <li>Longitude: <b>-6.7200711</b></li>
     *   <li>Easting: <b>2800000</b></li>
     *   <li>Northing: <b>1000000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-43 : REVERSE")
    public void GIGS_5111_43() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{2800000, 1000000};
        final double[] destinationPoint = new double[]{0.9071392, -6.7200711};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-44” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-44</b></li>
     *   <li>Latitude: <b>0</b></li>
     *   <li>Longitude: <b>3.1922806</b></li>
     *   <li>Easting: <b>3900000</b></li>
     *   <li>Northing: <b>900000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-44 : FORWARD")
    public void GIGS_5111_44() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{0, 3.1922806};
        final double[] destinationPoint = new double[]{3900000, 900000};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-45” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-45</b></li>
     *   <li>Latitude: <b>-0.9071392</b></li>
     *   <li>Longitude: <b>-6.7200711</b></li>
     *   <li>Easting: <b>2800000</b></li>
     *   <li>Northing: <b>800000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-45 : REVERSE")
    public void GIGS_5111_45() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{2800000, 800000};
        final double[] destinationPoint = new double[]{-0.9071392, -6.7200711};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-46” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-46</b></li>
     *   <li>Latitude: <b>-1.8140483</b></li>
     *   <li>Longitude: <b>-6.7200711</b></li>
     *   <li>Easting: <b>2800000</b></li>
     *   <li>Northing: <b>700000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-46 : FORWARD")
    public void GIGS_5111_46() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-1.8140483, -6.7200711};
        final double[] destinationPoint = new double[]{2800000, 700000};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-47” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-47</b></li>
     *   <li>Latitude: <b>-2</b></li>
     *   <li>Longitude: <b>-6.7200711</b></li>
     *   <li>Easting: <b>2800000</b></li>
     *   <li>Northing: <b>679490.646</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-47 : REVERSE")
    public void GIGS_5111_47() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{2800000, 679490.646};
        final double[] destinationPoint = new double[]{-2, -6.7200711};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-48” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-48</b></li>
     *   <li>Latitude: <b>-3.6262553</b></li>
     *   <li>Longitude: <b>-6.7200711</b></li>
     *   <li>Easting: <b>2800000</b></li>
     *   <li>Northing: <b>500000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-48 : FORWARD")
    public void GIGS_5111_48() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-3.6262553, -6.7200711};
        final double[] destinationPoint = new double[]{2800000, 500000};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-49” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-49</b></li>
     *   <li>Latitude: <b>-4.531095</b></li>
     *   <li>Longitude: <b>-6.7200711</b></li>
     *   <li>Easting: <b>2800000</b></li>
     *   <li>Northing: <b>400000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-49 : REVERSE")
    public void GIGS_5111_49() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{2800000, 400000};
        final double[] destinationPoint = new double[]{-4.531095, -6.7200711};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-50” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-50</b></li>
     *   <li>Latitude: <b>-5.4347892</b></li>
     *   <li>Longitude: <b>-6.7200711</b></li>
     *   <li>Easting: <b>2800000</b></li>
     *   <li>Northing: <b>300000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-50 : FORWARD")
    public void GIGS_5111_50() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-5.4347892, -6.7200711};
        final double[] destinationPoint = new double[]{2800000, 300000};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-51” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-51</b></li>
     *   <li>Latitude: <b>-6.3371111</b></li>
     *   <li>Longitude: <b>-6.7200711</b></li>
     *   <li>Easting: <b>2800000</b></li>
     *   <li>Northing: <b>200000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-51 : REVERSE")
    public void GIGS_5111_51() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{2800000, 200000};
        final double[] destinationPoint = new double[]{-6.3371111, -6.7200711};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-52” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-52</b></li>
     *   <li>Latitude: <b>-7.2378372</b></li>
     *   <li>Longitude: <b>-6.7200711</b></li>
     *   <li>Easting: <b>2800000</b></li>
     *   <li>Northing: <b>100000</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-52 : FORWARD")
    public void GIGS_5111_52() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-7.2378372, -6.7200711};
        final double[] destinationPoint = new double[]{2800000, 100000};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-53” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-53</b></li>
     *   <li>Latitude: <b>-8.136745</b></li>
     *   <li>Longitude: <b>-31.9515111</b></li>
     *   <li>Easting: <b>0</b></li>
     *   <li>Northing: <b>0</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-53 : REVERSE")
    public void GIGS_5111_53() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{0, 0};
        final double[] destinationPoint = new double[]{-8.136745, -31.9515111};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-54” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-54</b></li>
     *   <li>Latitude: <b>-2</b></li>
     *   <li>Longitude: <b>-177.8077194</b></li>
     *   <li>Easting: <b>23764105.84</b></li>
     *   <li>Northing: <b>679490.646</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-54 : REVERSE")
    public void GIGS_5111_54() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{23764105.84, 679490.646};
        final double[] destinationPoint = new double[]{-2, -177.8077194};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-55” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-55</b></li>
     *   <li>Latitude: <b>-2</b></li>
     *   <li>Longitude: <b>163.1922806</b></li>
     *   <li>Easting: <b>21655625.33</b></li>
     *   <li>Northing: <b>679490.646</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-55 : FORWARD")
    public void GIGS_5111_55() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-2, 163.1922806};
        final double[] destinationPoint = new double[]{21655625.33, 679490.646};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-56” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-56</b></li>
     *   <li>Latitude: <b>-2</b></li>
     *   <li>Longitude: <b>133.1922806</b></li>
     *   <li>Easting: <b>18326445.58</b></li>
     *   <li>Northing: <b>679490.646</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-56 : REVERSE")
    public void GIGS_5111_56() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{18326445.58, 679490.646};
        final double[] destinationPoint = new double[]{-2, 133.1922806};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-57” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-57</b></li>
     *   <li>Latitude: <b>-2</b></li>
     *   <li>Longitude: <b>103.1922806</b></li>
     *   <li>Easting: <b>14997265.83</b></li>
     *   <li>Northing: <b>679490.646</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-57 : FORWARD")
    public void GIGS_5111_57() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-2, 103.1922806};
        final double[] destinationPoint = new double[]{14997265.83, 679490.646};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-58” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-58</b></li>
     *   <li>Latitude: <b>-2</b></li>
     *   <li>Longitude: <b>73.1922806</b></li>
     *   <li>Easting: <b>11668086.08</b></li>
     *   <li>Northing: <b>679490.646</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-58 : REVERSE")
    public void GIGS_5111_58() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{11668086.08, 679490.646};
        final double[] destinationPoint = new double[]{-2, 73.1922806};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-59” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-59</b></li>
     *   <li>Latitude: <b>-2</b></li>
     *   <li>Longitude: <b>43.1922806</b></li>
     *   <li>Easting: <b>8338906.333</b></li>
     *   <li>Northing: <b>679490.646</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-59 : FORWARD")
    public void GIGS_5111_59() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-2, 43.1922806};
        final double[] destinationPoint = new double[]{8338906.333, 679490.646};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-60” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-60</b></li>
     *   <li>Latitude: <b>-2</b></li>
     *   <li>Longitude: <b>13.1922806</b></li>
     *   <li>Easting: <b>5009726.583</b></li>
     *   <li>Northing: <b>679490.646</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-60 : REVERSE")
    public void GIGS_5111_60() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{5009726.583, 679490.646};
        final double[] destinationPoint = new double[]{-2, 13.1922806};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-61” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-61</b></li>
     *   <li>Latitude: <b>-2</b></li>
     *   <li>Longitude: <b>3.1922806</b></li>
     *   <li>Easting: <b>3900000</b></li>
     *   <li>Northing: <b>679490.646</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-61 : FORWARD")
    public void GIGS_5111_61() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-2, 3.1922806};
        final double[] destinationPoint = new double[]{3900000, 679490.646};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-62” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-62</b></li>
     *   <li>Latitude: <b>-2</b></li>
     *   <li>Longitude: <b>0</b></li>
     *   <li>Easting: <b>3545744.141</b></li>
     *   <li>Northing: <b>679490.646</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-62 : REVERSE")
    public void GIGS_5111_62() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{3545744.141, 679490.646};
        final double[] destinationPoint = new double[]{-2, 0};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-63” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-63</b></li>
     *   <li>Latitude: <b>-2</b></li>
     *   <li>Longitude: <b>-6.7200711</b></li>
     *   <li>Easting: <b>2800000</b></li>
     *   <li>Northing: <b>679490.646</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-63 : FORWARD")
    public void GIGS_5111_63() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-2, -6.7200711};
        final double[] destinationPoint = new double[]{2800000, 679490.646};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-64” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-64</b></li>
     *   <li>Latitude: <b>-2</b></li>
     *   <li>Longitude: <b>-16.8077194</b></li>
     *   <li>Easting: <b>1680546.833</b></li>
     *   <li>Northing: <b>679490.646</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-64 : REVERSE")
    public void GIGS_5111_64() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{1680546.833, 679490.646};
        final double[] destinationPoint = new double[]{-2, -16.8077194};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-65” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-65</b></li>
     *   <li>Latitude: <b>-2</b></li>
     *   <li>Longitude: <b>-46.8077194</b></li>
     *   <li>Easting: <b>-1648632.916</b></li>
     *   <li>Northing: <b>679490.646</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-65 : FORWARD")
    public void GIGS_5111_65() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-2, -46.8077194};
        final double[] destinationPoint = new double[]{-1648632.916, 679490.646};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-66” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-66</b></li>
     *   <li>Latitude: <b>-2</b></li>
     *   <li>Longitude: <b>-76.8077194</b></li>
     *   <li>Easting: <b>-4977812.666</b></li>
     *   <li>Northing: <b>679490.646</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-66 : REVERSE")
    public void GIGS_5111_66() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-4977812.666, 679490.646};
        final double[] destinationPoint = new double[]{-2, -76.8077194};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-67” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-67</b></li>
     *   <li>Latitude: <b>-2</b></li>
     *   <li>Longitude: <b>-106.8077194</b></li>
     *   <li>Easting: <b>-8306992.416</b></li>
     *   <li>Northing: <b>679490.646</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-67 : FORWARD")
    public void GIGS_5111_67() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-2, -106.8077194};
        final double[] destinationPoint = new double[]{-8306992.416, 679490.646};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-68” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-68</b></li>
     *   <li>Latitude: <b>-2</b></li>
     *   <li>Longitude: <b>-136.8077194</b></li>
     *   <li>Easting: <b>-11636172.17</b></li>
     *   <li>Northing: <b>679490.646</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-68 : REVERSE")
    public void GIGS_5111_68() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-11636172.17, 679490.646};
        final double[] destinationPoint = new double[]{-2, -136.8077194};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-69” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-69</b></li>
     *   <li>Latitude: <b>-2</b></li>
     *   <li>Longitude: <b>-166.8077194</b></li>
     *   <li>Easting: <b>-14965351.92</b></li>
     *   <li>Northing: <b>679490.646</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-69 : FORWARD")
    public void GIGS_5111_69() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{-2, -166.8077194};
        final double[] destinationPoint = new double[]{-14965351.92, 679490.646};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5111-70” for MercA calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5111-70</b></li>
     *   <li>Latitude: <b>-2</b></li>
     *   <li>Longitude: <b>-175.8077194</b></li>
     *   <li>Easting: <b>-15964105.84</b></li>
     *   <li>Northing: <b>679490.646</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5111-70 : REVERSE")
    public void GIGS_5111_70() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{-15964105.84, 679490.646};
        final double[] destinationPoint = new double[]{-2, -175.8077194};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }
}
