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
 * Code generator for {@link org.iogp.gigs.Test5201}. This generator needs to be executed only if the GIGS data changed.
 * The code is sent to the standard output; maintainers need to copy-and-paste the relevant methods to the test class,
 * but be aware that the original code may contain manual changes that need to be preserved.
 *
 * @author  Estelle Idée (Geomatys)
 * @version 1.0
 * @since   1.0
 */
public final class Test5201 extends TestMethodGenerator {
    /**
     * Launcher.
     *
     * @param  args  ignored.
     * @throws IOException if an error occurred while reading the test data.
     */
    public static void main(String[] args) throws IOException {
        new Test5201().run();
    }

    /**
     * Creates a new test methods generator.
     */
    private Test5201() {}

    /**
     * Generates the code.
     *
     * @throws IOException if an error occurred while reading the test data.
     */
    private void run() throws IOException {
        final DataParser data = new DataParser(Series.TRANSFORMATION, "GIGS_tfm_5201_GeogGeocen_output.txt",
                String.class,      // [ 0]: Point
                Double .class,     // [ 1]: Geocentric X (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813)
                Double .class,     // [ 2]: Geocentric Y (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813)
                Double .class,     // [ 3]: Geocentric Z (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); metre; EPSG CRS code 4813)
                Double .class,     // [ 4]: Latitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330)
                Double .class,     // [ 5]: Longitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330)
                Double .class,     // [ 6]: Ellipsoidal height (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330)
                String.class,      // [ 7]: Transect
                String .class,     // [ 8]: Transformation Direction
                String .class);    // [ 9]: GIGS Remarks

        final DecimalFormat degreeDf = new DecimalFormat("#.#########", DecimalFormatSymbols.getInstance(Locale.US));
        final DecimalFormat meterDf = new DecimalFormat("#.###", DecimalFormatSymbols.getInstance(Locale.US));
        while (data.next()) {
            final String point          = data.getString(0);
            final Double geocentricX    = data.getDouble(1);
            final Double geocentricY    = data.getDouble(2);
            final Double geocentricZ    = data.getDouble(3);
            final Double latitude       = data.getDouble(4);
            final Double longitude      = data.getDouble(5);
            final Double ellipsoidal    = data.getDouble(6);
            final String transect       = data.getString(7);
            final String direction      = data.getString(8);
            final String remarks        = data.getString(9);

            final String geocentricXStr = meterDf.format(geocentricX);
            final String geocentricYStr = meterDf.format(geocentricY);
            final String geocentricZStr = meterDf.format(geocentricZ);
            final String latitudeStr = degreeDf.format(latitude);
            final String longitudeStr = degreeDf.format(longitude);
            final String ellipsoidalStr = meterDf.format(ellipsoidal);
            final boolean isForward = "FORWARD".equals(direction);
            final boolean isRTC = remarks != null && remarks.contains("Round Trip calculation point");

            /*
             * Write javadoc.
             */
            out.append('\n');
            indent(1); out.append("/**\n");
            indent(1); out.append(" * Tests “").append(point).append("” for Geographic Geocentric transformation outputs based on the factory.\n");
            indent(1); out.append(" *\n");
            printJavadocKeyValues("Point", point,
                    "Geocentric X (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813)", geocentricXStr,
                    "Geocentric Y (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); decimal degree; EPSG CRS code 4813)", geocentricYStr,
                    "Geocentric Z (GIGS CRS Code 64007; GIGS geogCRS D; Batavia (Jakarta); metre; EPSG CRS code 4813)", geocentricZStr,
                    "Latitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330)", latitudeStr,
                    "Longitude (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330)", longitudeStr,
                    "Ellipsoidal height (GIGS CRS Code 62012; GIGS projCRS D5; Batavia (Jakarta) / NEIEZ; metre; EPSG CRS code 5330)", ellipsoidalStr,
                    "Transect", transect,
                    "Transformation direction", direction);
            printRemarks(remarks);
            final HashMap<String, String> errors = new HashMap<>(1);
            errors.put("TransformException", "if an error occurred while transforming the original point.");
            printJavadocThrows(errors);
            /*
             * Write test method.
             */
            printTestMethodSignature(point, point.concat(" : ").concat(direction), List.of("TransformException"));
            indent(2); out.append("isRTC = ").append(isRTC).append(";\n");
            indent(2); out.append("isForward = ").append(isForward).append(";\n");
            final StringBuilder originPoint;
            final StringBuilder destinationPoint;
            if (isForward) {
                originPoint = new StringBuilder("new double[]{").append(geocentricXStr).append(", ").append(geocentricYStr).append(", ").append(geocentricZStr).append("}");
                destinationPoint = new StringBuilder("new double[]{").append(latitudeStr).append(", ").append(longitudeStr).append(", ").append(ellipsoidalStr).append("}");
            } else {
                originPoint = new StringBuilder("new double[]{").append(latitudeStr).append(", ").append(longitudeStr).append(", ").append(ellipsoidalStr).append("}");
                destinationPoint = new StringBuilder("new double[]{").append(geocentricXStr).append(", ").append(geocentricYStr).append(", ").append(geocentricZStr).append("}");
            }
            indent(2); out.append("final double[] originPoint = ").append(originPoint).append(";\n");
            indent(2); out.append("final double[] destinationPoint = ").append(destinationPoint).append(";\n");
            if (!isRTC) {
                indent(2);out.append("final double[] res = convertPoint(originPoint);\n");
                indent(2);out.append("verifyConversion(destinationPoint, res);\n");
            } else {
                indent(2); out.append("convertAndVerifyRoundTripPoint(originPoint, destinationPoint);\n");
            }
            indent(1); out.append('}');
            saveTestMethod();
        }
        flushAllMethods();
    }
}
