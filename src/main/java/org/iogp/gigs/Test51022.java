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
 *   <td><a href="https://github.com/IOGP-GIGS/GIGSTestDataset/blob/main/GIGSTestDatasetFiles/GIGS%205100%20Conversion%20test%20data/ASCII/GIGS_conv_5102_LCC1_input_part2.txt">{@code GIGS_conv_5102_LCC1_input_part2.txt}</a>
 * </tr><tr>
 *   <th>Tested API:</th>
 *   <td>{@link CoordinateOperationFactory#createOperation(CoordinateReferenceSystem, CoordinateReferenceSystem)}.</td>
 * </tr><tr>
 *   <th>Expected result:</th>
 *   <td>Results for the forward and reverse calculations should agree to within 0.03m (30mm) or
 *   0.0000003° of the Test Data. See file GIGS_conv_5102_LCC1_output_part2.
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
 * public class MyTest extends Test51022 {
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
public class Test51022 extends Series5100 {

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
    public Test51022(final Factories factories) throws FactoryException {
        super(factories, 0.03, 0.0000003, 0.006, 0.00000006);
    }

    /**
     * Method used in class instantiation to initialise the geographic CRS and the projected CRS
     * used in this test class.
     * <p>Geographic CRS : GIGS CRS Code 64011; GIGS geogCRS H; NTF (Paris); gradians; EPSG CRS code 4807
     * <p>ProjectedCRS : GIGS CRS Code 62026; GIGS projCRS H19; NTF (Paris) / Lambert zone II; metre; EPSG CRS code 27572
     *
     * @throws FactoryException if an error occurred while creating a CRS.
     */
    @Override
    protected void createCRSs() throws FactoryException {
        createGeogCRS(Test3205::GIGS_64011);
        createProjCRS(Test3207::GIGS_62026);
    }

    /**
     * Tests “GIGS-5102-20” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-20</b></li>
     *   <li>Latitude: <b>64.44444444</b></li>
     *   <li>Longitude: <b>2.958634256</b></li>
     *   <li>Easting: <b>760724.023</b></li>
     *   <li>Northing: <b>3457334.864</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-20 : FORWARD")
    public void GIGS_5102_20() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{64.44444444, 2.958634256};
        final double[] destinationPoint = new double[]{760724.023, 3457334.864};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5102-21” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-21</b></li>
     *   <li>Latitude: <b>63.33333333</b></li>
     *   <li>Longitude: <b>2.958634256</b></li>
     *   <li>Easting: <b>764567.882</b></li>
     *   <li>Northing: <b>3343917.044</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-21 : REVERSE")
    public void GIGS_5102_21() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{764567.882, 3343917.044};
        final double[] destinationPoint = new double[]{63.33333333, 2.958634256};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5102-22” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-22</b></li>
     *   <li>Latitude: <b>62.22222222</b></li>
     *   <li>Longitude: <b>2.958634256</b></li>
     *   <li>Easting: <b>768397.648</b></li>
     *   <li>Northing: <b>3230915.06</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-22 : FORWARD")
    public void GIGS_5102_22() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{62.22222222, 2.958634256};
        final double[] destinationPoint = new double[]{768397.648, 3230915.06};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5102-23” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-23</b></li>
     *   <li>Latitude: <b>61.11111111</b></li>
     *   <li>Longitude: <b>2.958634256</b></li>
     *   <li>Easting: <b>772214.859</b></li>
     *   <li>Northing: <b>3118283.535</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-23 : REVERSE")
    public void GIGS_5102_23() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{772214.859, 3118283.535};
        final double[] destinationPoint = new double[]{61.11111111, 2.958634256};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5102-24” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-24</b></li>
     *   <li>Latitude: <b>60</b></li>
     *   <li>Longitude: <b>2.958634256</b></li>
     *   <li>Easting: <b>776020.989</b></li>
     *   <li>Northing: <b>3005978.979</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-24 : FORWARD")
    public void GIGS_5102_24() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{60, 2.958634256};
        final double[] destinationPoint = new double[]{776020.989, 3005978.979};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5102-25” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-25</b></li>
     *   <li>Latitude: <b>58.88888889</b></li>
     *   <li>Longitude: <b>2.958634256</b></li>
     *   <li>Easting: <b>779817.454</b></li>
     *   <li>Northing: <b>2893959.584</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-25 : REVERSE")
    public void GIGS_5102_25() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{779817.454, 2893959.584};
        final double[] destinationPoint = new double[]{58.88888889, 2.958634256};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5102-26” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-26</b></li>
     *   <li>Latitude: <b>56.66666667</b></li>
     *   <li>Longitude: <b>1.847523144</b></li>
     *   <li>Easting: <b>717027.602</b></li>
     *   <li>Northing: <b>2668679.866</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-26 : FORWARD")
    public void GIGS_5102_26() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{56.66666667, 1.847523144};
        final double[] destinationPoint = new double[]{717027.602, 2668679.866};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5102-27” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-27</b></li>
     *   <li>Latitude: <b>55.55555556</b></li>
     *   <li>Longitude: <b>1.847523144</b></li>
     *   <li>Easting: <b>719385.487</b></li>
     *   <li>Northing: <b>2557240.347</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-27 : REVERSE")
    public void GIGS_5102_27() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{719385.487, 2557240.347};
        final double[] destinationPoint = new double[]{55.55555556, 1.847523144};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5102-28” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-28</b></li>
     *   <li>Latitude: <b>54.44444444</b></li>
     *   <li>Longitude: <b>1.847523144</b></li>
     *   <li>Easting: <b>721740.59</b></li>
     *   <li>Northing: <b>2445932.319</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-28 : FORWARD")
    public void GIGS_5102_28() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{54.44444444, 1.847523144};
        final double[] destinationPoint = new double[]{721740.59, 2445932.319};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5102-29” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-29</b></li>
     *   <li>Latitude: <b>52</b></li>
     *   <li>Longitude: <b>1.847523144</b></li>
     *   <li>Easting: <b>726915.726</b></li>
     *   <li>Northing: <b>2201342.518</b></li>
     *   <li>Transect: <b>A</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-29 : REVERSE")
    public void GIGS_5102_29() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{726915.726, 2201342.518};
        final double[] destinationPoint = new double[]{52, 1.847523144};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5102-30” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-30</b></li>
     *   <li>Latitude: <b>58.88888889</b></li>
     *   <li>Longitude: <b>0.736412033</b></li>
     *   <li>Easting: <b>644765.081</b></li>
     *   <li>Northing: <b>2891102.088</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-30 : FORWARD")
    public void GIGS_5102_30() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{58.88888889, 0.736412033};
        final double[] destinationPoint = new double[]{644765.081, 2891102.088};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5102-31” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-31</b></li>
     *   <li>Latitude: <b>58.88888889</b></li>
     *   <li>Longitude: <b>1.847523144</b></li>
     *   <li>Easting: <b>712300.356</b></li>
     *   <li>Northing: <b>2892101.266</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-31 : REVERSE")
    public void GIGS_5102_31() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{712300.356, 2892101.266};
        final double[] destinationPoint = new double[]{58.88888889, 1.847523144};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5102-32” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-32</b></li>
     *   <li>Latitude: <b>58.88888889</b></li>
     *   <li>Longitude: <b>2.958634256</b></li>
     *   <li>Easting: <b>779817.454</b></li>
     *   <li>Northing: <b>2893959.584</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-32 : FORWARD")
    public void GIGS_5102_32() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{58.88888889, 2.958634256};
        final double[] destinationPoint = new double[]{779817.454, 2893959.584};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5102-33” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-33</b></li>
     *   <li>Latitude: <b>58.88888889</b></li>
     *   <li>Longitude: <b>4.069745367</b></li>
     *   <li>Easting: <b>847305.444</b></li>
     *   <li>Northing: <b>2896676.742</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-33 : REVERSE")
    public void GIGS_5102_33() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{847305.444, 2896676.742};
        final double[] destinationPoint = new double[]{58.88888889, 4.069745367};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5102-34” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-34</b></li>
     *   <li>Latitude: <b>58.88888889</b></li>
     *   <li>Longitude: <b>5.180856478</b></li>
     *   <li>Easting: <b>914753.403</b></li>
     *   <li>Northing: <b>2900252.301</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-34 : FORWARD")
    public void GIGS_5102_34() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{58.88888889, 5.180856478};
        final double[] destinationPoint = new double[]{914753.403, 2900252.301};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5102-35” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-35</b></li>
     *   <li>Latitude: <b>58.88888889</b></li>
     *   <li>Longitude: <b>6.291967589</b></li>
     *   <li>Easting: <b>982150.413</b></li>
     *   <li>Northing: <b>2904685.68</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-35 : REVERSE")
    public void GIGS_5102_35() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{982150.413, 2904685.68};
        final double[] destinationPoint = new double[]{58.88888889, 6.291967589};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5102-36” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-36</b></li>
     *   <li>Latitude: <b>58.88888889</b></li>
     *   <li>Longitude: <b>7.4030787</b></li>
     *   <li>Easting: <b>1049485.565</b></li>
     *   <li>Northing: <b>2909976.163</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-36 : FORWARD")
    public void GIGS_5102_36() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{58.88888889, 7.4030787};
        final double[] destinationPoint = new double[]{1049485.565, 2909976.163};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5102-37” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-37</b></li>
     *   <li>Latitude: <b>58.88888889</b></li>
     *   <li>Longitude: <b>8.514189811</b></li>
     *   <li>Easting: <b>1116747.958</b></li>
     *   <li>Northing: <b>2916122.894</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>REVERSE</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-37 : REVERSE")
    public void GIGS_5102_37() throws TransformException {
        isRTC = false;
        isForward = false;
        final double[] originPoint = new double[]{1116747.958, 2916122.894};
        final double[] destinationPoint = new double[]{58.88888889, 8.514189811};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }

    /**
     * Tests “GIGS-5102-38” for LCC1 calculation outputs based on the factory.
     *
     * <ul>
     *   <li>Point: <b>GIGS-5102-38</b></li>
     *   <li>Latitude: <b>58.88888889</b></li>
     *   <li>Longitude: <b>9.625300922</b></li>
     *   <li>Easting: <b>1183926.705</b></li>
     *   <li>Northing: <b>2923124.876</b></li>
     *   <li>Transect: <b>B</b></li>
     *   <li>Conversion direction: <b>FORWARD</b></li>
     * </ul>
     *
     * @throws TransformException if an error occurred while converting the original point.
     */
    @Test
    @DisplayName("GIGS-5102-38 : FORWARD")
    public void GIGS_5102_38() throws TransformException {
        isRTC = false;
        isForward = true;
        final double[] originPoint = new double[]{58.88888889, 9.625300922};
        final double[] destinationPoint = new double[]{1183926.705, 2923124.876};
        final double[] res = convertPoint(originPoint);
        verifyConversion(destinationPoint, res);
    }
}
