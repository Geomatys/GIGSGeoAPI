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
package org.iogp.gigs.generator;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


/**
 * Code generator for {@link org.iogp.gigs.Test52071}. This generator needs to be executed only if the GIGS data changed.
 * The code is sent to the standard output; maintainers need to copy-and-paste the relevant methods to the test class,
 * but be aware that the original code may contain manual changes that need to be preserved.
 *
 * @author  Estelle Idée (Geomatys)
 * @version 1.0
 * @since   1.0
 */
public final class Test52071 extends TestMethodGenerator {
    /**
     * Launcher.
     *
     * @param  args  ignored.
     * @throws IOException if an error occurred while reading the test data.
     */
    public static void main(String[] args) throws IOException {
        new Test52071().run();
    }

    /**
     * Creates a new test methods generator.
     */
    private Test52071() {}

    /**
     * Generates the code.
     *
     * @throws IOException if an error occurred while reading the test data.
     */
    private void run() throws IOException {
        final DataParser data = new DataParser(Series.TRANSFORMATION, "GIGS_tfm_5207_NTv2_output_part1.txt",
                String.class,  // [ 0]: Point
                Double.class,  // [ 1]: Latitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202)
                Double.class,  // [ 2]: Longitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202)
                Double.class,  // [ 3]: Latitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283)
                Double.class,  // [ 4]: Longitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283)
                String.class,  // [ 5]: Transect
                String.class,  // [ 6]: Transformation Direction
                Boolean.class, // [ 7]: Value outside tfm grid; should fail
                String.class); // [ 8]: GIGS Remarks

        final DecimalFormat degreeDf = new DecimalFormat("#.#########", DecimalFormatSymbols.getInstance(Locale.US));

        while (data.next()) {
            final String point          = data.getString(0);
            final Double latitude1      = data.getDouble(1);
            final Double longitude1     = data.getDouble(2);
            final Double latitude2      = data.getDouble(3);
            final Double longitude2     = data.getDouble(4);
            final String transect       = data.getString(5);
            final String direction      = data.getString(6);
            final Boolean shouldFail        = data.getBoolean(7);
            final String remarks        = data.getString(8);

            final String latitude1Str = degreeDf.format(latitude1);
            final String longitude1Str = degreeDf.format(longitude1);
            final String latitude2Str = degreeDf.format(latitude2);
            final String longitude2Str = degreeDf.format(longitude2);
            final boolean isForward = "FORWARD".equals(direction);
            final boolean isRTC = remarks != null && remarks.contains("Round Trip calculation point");

            /*
             * Write javadoc.
             */
            out.append('\n');
            indent(1); out.append("/**\n");
            indent(1); out.append(" * Tests “").append(point).append("” for NTv2 transformation outputs based on the factory.\n");
            indent(1); out.append(" *\n");
            printJavadocKeyValues("Point", point,
                    "Latitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202)", latitude1Str,
                    "Longitude (GIGS CRS Code 64016; GIGS geogCRS X; AGD66; decimal degree; EPSG CRS code 4202)", longitude1Str,
                    "Latitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283)", latitude2Str,
                    "Longitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283)", longitude2Str,
                    "Transect", transect,
                    "Transformation direction", direction,
                    "Value outside tfm grid; should fail", shouldFail);
            printRemarks(remarks);

            if (!shouldFail) {
                final HashMap<String, String> errors = new HashMap<>(1);
                errors.put("TransformException", "if an error occurred while converting the original point.");
                printJavadocThrows(errors);
            }
            /*
             * Write test method.
             */
            printTestMethodSignature(point, point.concat(" : ").concat(direction), List.of("TransformException"));
            indent(2); out.append("isRTC = ").append(isRTC).append(";\n");
            indent(2); out.append("isForward = ").append(isForward).append(";\n");
            final StringBuilder originPoint;
            final StringBuilder destinationPoint;
            if (isForward) {
                originPoint = new StringBuilder("new double[]{").append(latitude1Str).append(", ").append(longitude1Str).append("}");
                destinationPoint = new StringBuilder("new double[]{").append(latitude2Str).append(", ").append(longitude2Str).append("}");
            } else {
                originPoint = new StringBuilder("new double[]{").append(latitude2Str).append(", ").append(longitude2Str).append("}");
                destinationPoint = new StringBuilder("new double[]{").append(latitude1Str).append(", ").append(longitude1Str).append("}");
            }
            indent(2); out.append("final double[] originPoint = ").append(originPoint).append(";\n");
            if (shouldFail) {
                indent(2);out.append("Assertions.assertThrowsExactly(TransformException.class, () -> convertPoint(originPoint));\n");
            } else {
                indent(2); out.append("final double[] destinationPoint = ").append(destinationPoint).append(";\n");
                if (!isRTC) {
                    indent(2);
                    out.append("final double[] res = convertPoint(originPoint);\n");
                    indent(2);
                    out.append("verifyConversion(destinationPoint, res);\n");
                } else {
                    indent(2);
                    out.append("convertAndVerifyRoundTripPoint(originPoint, destinationPoint);\n");
                }
            }
            indent(1); out.append('}');
            saveTestMethod();
        }
        flushAllMethods();
    }
}
