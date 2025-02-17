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
 * Code generator for tests from the series 5200.
 *
 * @author  Estelle Idée (Geomatys)
 * @version 1.0
 * @since   1.0
 */
public abstract class TestSeries5200 extends TestMethodGenerator {

    /**
     * Creates a new test methods generator.
     */
    TestSeries5200() {
    }

    /**
     * Generates the code.
     * @param data the dataParser to read the output file entries.
     * @param conversionMethod the conversion shortname.
     * @throws IOException if an error occurred while reading the test data.
     */
    void run(final DataParser data, final String conversionMethod) throws IOException {
        final DecimalFormat degreeDf = new DecimalFormat("#.#########", DecimalFormatSymbols.getInstance(Locale.US));
        final DecimalFormat meterDf = new DecimalFormat("#.###", DecimalFormatSymbols.getInstance(Locale.US));
        while (data.next()) {
            final String point          = data.getString(0);
            final Double latitude       = data.getDouble(1);
            final Double longitude      = data.getDouble(2);
            final Double easting        = data.getDouble(3);
            final Double northing       = data.getDouble(4);
            final String transect       = data.getString(5);
            final String direction      = data.getString(6);
            final String remarks        = data.getString(7);

            /*
             * Write javadoc.
             */
            final String latitudeStr = degreeDf.format(latitude);
            final String longitudeStr = degreeDf.format(longitude);
            final String eastingStr = meterDf.format(easting);
            final String northingStr = meterDf.format(northing);
            final boolean isForward = "FORWARD".equals(direction);
            out.append('\n');
            indent(1); out.append("/**\n");
            indent(1); out.append(" * Tests “").append(point).append("” for ").append(conversionMethod).append(" calculation outputs based on the factory.\n");
            indent(1); out.append(" *\n");
            printJavadocKeyValues("Point", point,
                    "Latitude", latitudeStr,
                    "Longitude", longitudeStr,
                    "Easting", eastingStr,
                    "Northing", northingStr,
                    "Transect", transect,
                    "Conversion direction", direction);
            printRemarks(remarks);
            final HashMap<String, String> errors = new HashMap<>(1);
            errors.put("TransformException", "if an error occurred while converting the original point.");
            printJavadocThrows(errors);
            /*
             * Write test method.
             */
            printTestMethodSignature(point, point.concat(" : ").concat(direction), List.of("TransformException"));
            final boolean isRTC = "Round Trip calculation point".equalsIgnoreCase(remarks);
            indent(2); out.append("isRTC = ").append(isRTC).append(";\n");
            indent(2); out.append("isForward = ").append(isForward).append(";\n");
            final StringBuilder originPoint;
            final StringBuilder destinationPoint;
            if (isForward) {
                originPoint = new StringBuilder("new double[]{").append(latitudeStr).append(", ").append(longitudeStr).append("}");
                destinationPoint = new StringBuilder("new double[]{").append(eastingStr).append(", ").append(northingStr).append("}");
            } else {
                originPoint = new StringBuilder("new double[]{").append(eastingStr).append(", ").append(northingStr).append("}");
                destinationPoint = new StringBuilder("new double[]{").append(latitudeStr).append(", ").append(longitudeStr).append("}");
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
