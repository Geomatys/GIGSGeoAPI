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


/**
 * Code generator for {@link org.iogp.gigs.Test5109}. This generator needs to be executed only if the GIGS data changed.
 * The code is sent to the standard output; maintainers need to copy-and-paste the relevant methods to the test class,
 * but be aware that the original code may contain manual changes that need to be preserved.
 *
 * @author  Estelle Idée (Geomatys)
 * @version 1.0
 * @since   1.0
 */
public final class Test5109 extends TestSeries5100 {
    /**
     * Launcher.
     *
     * @param  args  ignored.
     * @throws IOException if an error occurred while reading the test data.
     */
    public static void main(String[] args) throws IOException {
        new Test5109().run();
    }

    /**
     * Creates a new test methods generator.
     */
    private Test5109() {}

    /**
     * Generates the code.
     *
     * @throws IOException if an error occurred while reading the test data.
     */
    private void run() throws IOException {
        final DataParser data = new DataParser(Series.CONVERSION, "GIGS_conv_5109_Albers_output.txt",
                String.class,      // [ 0]: Point
                Double .class,     // [ 1]: Latitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283)
                Double .class,     // [ 2]: Longitude (GIGS CRS Code 64009; GIGS geogCRS F; GDA94; decimal degree; EPSG CRS code 4283)
                Double .class,     // [ 3]: Easting (GIGS CRS Code 62016; GIGS projCRS F9; GDA94 / Australian Albers; metre; EPSG CRS code 3577)
                Double .class,     // [ 4]: Northing (GIGS CRS Code 62016; GIGS projCRS F9; GDA94 / Australian Albers; metre; EPSG CRS code 3577)
                String.class,      // [ 5]: Transect
                String .class,     // [ 6]: Conversion Direction
                String .class);    // [ 7]: GIGS Remarks

        super.run(data, "Albers");
    }
}
