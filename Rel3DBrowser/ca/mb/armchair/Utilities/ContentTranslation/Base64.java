package ca.mb.armchair.Utilities.ContentTranslation;

/*
 * Base64 encoding and decoding.
 * Copyright (C) 2001 Stephen Ostermiller <utils@Ostermiller.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * See COPYING.TXT for details.
 */

import java.io.*;

/**
 * Implements Base64 encoding and decoding as defined by RFC 2045: "Multipurpose Interne
 * Mail Extensions (MIME) Part One: Format of Internet Message Bodies" page 23.
 * More information about this class is available from <a href=
 * "http://ostermiller.org/utils/Base64.html">ostermiller.org</a>.
 *
 * <blockquote>
 * <p>The Base64 Content-Transfer-Encoding is designed to represent
 * arbitrary sequences of octets in a form that need not be humanly
 * readable.  The encoding and decoding algorithms are simple, but the
 * encoded data are consistently only about 33 percent larger than the
 * unencoded data.  This encoding is virtually identical to the one used
 * in Privacy Enhanced Mail (PEM) applications, as defined in RFC 1421.</p>
 *
 * <p>A 65-character subset of US-ASCII is used, enabling 6 bits to be
 * represented per printable character. (The extra 65th character, "=",
 * is used to signify a special processing function.)</p>
 *
 * <p>NOTE:  This subset has the important property that it is represented
 * identically in all versions of ISO 646, including US-ASCII, and all
 * characters in the subset are also represented identically in all
 * versions of EBCDIC. Other popular encodings, such as the encoding
 * used by the uuencode utility, Macintosh binhex 4.0 [RFC-1741], and
 * the base85 encoding specified as part of Level 2 PostScript, do no
 * share these properties, and thus do not fulfill the portability
 * requirements a binary transport encoding for mail must meet.</p>
 *
 * <p>The encoding process represents 24-bit groups of input bits as outpu
 * strings of 4 encoded characters.  Proceeding from left to right, a
 * 24-bit input group is formed by concatenating 3 8bit input groups.
 * These 24 bits are then treated as 4 concatenated 6-bit groups, each
 * of which is translated into a single digit in the base64 alphabet.
 * When encoding a bit stream via the base64 encoding, the bit stream
 * must be presumed to be ordered with the most-significant-bit first.
 * That is, the first bit in the stream will be the high-order bit in
 * the first 8bit byte, and the eighth bit will be the low-order bit in
 * the first 8bit byte, and so on.</p>
 *
 * <p>Each 6-bit group is used as an index into an array of 64 printable
 * characters.  The character referenced by the index is placed in the
 * output string.  These characters, identified in Table 1, below, are
 * selected so as to be universally representable, and the set excludes
 * characters with particular significance to SMTP (e.g., ".", CR, LF)
 * and to the multipart boundary delimiters defined in RFC 2046 (e.g.,
 * "-").</p>
 * <pre>
 *                  Table 1: The Base64 Alphabe
 *
 *   Value Encoding  Value Encoding  Value Encoding  Value Encoding
 *       0 A            17 R            34 i            51 z
 *       1 B            18 S            35 j            52 0
 *       2 C            19 T            36 k            53 1
 *       3 D            20 U            37 l            54 2
 *       4 E            21 V            38 m            55 3
 *       5 F            22 W            39 n            56 4
 *       6 G            23 X            40 o            57 5
 *       7 H            24 Y            41 p            58 6
 *       8 I            25 Z            42 q            59 7
 *       9 J            26 a            43 r            60 8
 *      10 K            27 b            44 s            61 9
 *      11 L            28 c            45 t            62 +
 *      12 M            29 d            46 u            63 /
 *      13 N            30 e            47 v
 *      14 O            31 f            48 w         (pad) =
 *      15 P            32 g            49 x
 *      16 Q            33 h            50 y
 * </pre>
 * <p>The encoded output stream must be represented in lines of no more
 * than 76 characters each.  All line breaks or other characters no
 * found in Table 1 must be ignored by decoding software.  In base64
 * data, characters other than those in Table 1, line breaks, and other
 * white space probably indicate a transmission error, about which a
 * warning message or even a message rejection might be appropriate
 * under some circumstances.</p>
 *
 * <p>Special processing is performed if fewer than 24 bits are available
 * at the end of the data being encoded.  A full encoding quantum is
 * always completed at the end of a body.  When fewer than 24 input bits
 * are available in an input group, zero bits are added (on the right)
 * to form an integral number of 6-bit groups.  Padding at the end of
 * the data is performed using the "=" character.  Since all base64
 * input is an integral number of octets, only the following cases can
 * arise: (1) the final quantum of encoding input is an integral
 * multiple of 24 bits; here, the final unit of encoded output will be
 * an integral multiple of 4 characters with no "=" padding, (2) the
 * final quantum of encoding input is exactly 8 bits; here, the final
 * unit of encoded output will be two characters followed by two "="
 * padding characters, or (3) the final quantum of encoding input is
 * exactly 16 bits; here, the final unit of encoded output will be three
 * characters followed by one "=" padding character.</p>
 *
 * <p>Because it is used only for padding at the end of the data, the
 * occurrence of any "=" characters may be taken as evidence that the
 * end of the data has been reached (without truncation in transit).  No
 * such assurance is possible, however, when the number of octets
 * transmitted was a multiple of three and no "=" characters are
 * present.</p>
 *
 * <p>Any characters outside of the base64 alphabet are to be ignored in
 * base64-encoded data.</p>
 *
 * <p>Care must be taken to use the proper octets for line breaks if base64
 * encoding is applied directly to text material that has not been
 * converted to canonical form.  In particular, text line breaks must be
 * converted into CRLF sequences prior to base64 encoding.  The
 * important thing to note is that this may be done directly by the
 * encoder rather than in a prior canonicalization step in some
 * implementations.</p>
 *
 * <p>NOTE: There is no need to worry about quoting potential boundary
 * delimiters within base64-encoded bodies within multipart entities
 * because no hyphen characters are used in the base64 encoding.</p>
 * </blockquote>
 */
public class Base64{

    /**
     * This class need not be instantiated, all methods are static.
     */
    private Base64(){
    }

    /**
     * Table of the sixty-four characters that are used as
     * the Base64 alphabet: [A-Za-z0-9+/]
     */
    protected static final byte[] base64Chars = {
        'A','B','C','D','E','F','G','H',
        'I','J','K','L','M','N','O','P',
        'Q','R','S','T','U','V','W','X',
        'Y','Z','a','b','c','d','e','f',
        'g','h','i','j','k','l','m','n',
        'o','p','q','r','s','t','u','v',
        'w','x','y','z','0','1','2','3',
        '4','5','6','7','8','9','+','/',
    };

    /**
     * Reverse lookup table for the Base64 alphabet.
     * reversebase64Chars[byte] gives n for the nth Base64
     * character or -1 if a character is not a Base64 character.
     */
    protected static final byte[] reverseBase64Chars = new byte[0xff];
    static {
        // Fill in -1 for all characters to start with
        for (int i=0; i<reverseBase64Chars.length; i++){
            reverseBase64Chars[i] = -1;
        }
        // For characters that are base64Chars, adjust
        // the reverse lookup table.
        for (byte i=0; i < base64Chars.length; i++){
            reverseBase64Chars[base64Chars[i]] = i;
        }
    }

    /**
     * Encode a String in Base64.
     * The String is converted to and from bytes according to the platform's
     * default character encoding.
     * No line breaks or other white space are inserted into the encoded data.
     *
     * @param string The data to encode.
     * @return An encoded String.
     */
    public static String encode(String string){
        return new String(encode(string.getBytes()));
    }

    /**
     * Encode a String in Base64.
     * No line breaks or other white space are inserted into the encoded data.
     *
     * @param string The data to encode.
     * @param enc Character encoding to use when converting to and from bytes.
     * @throws UnsupportedEncodingException if the character encoding specified is not supported.
     * @return An encoded String.
     */
    public static String encode(String string, String enc) throws UnsupportedEncodingException {
        return new String(encode(string.getBytes(enc)), enc);
    }

    /**
     * Encode bytes in Base64.
     * No line breaks or other white space are inserted into the encoded data.
     *
     * @param bytes The data to encode.
     * @return Encoded bytes.
     */
    public static byte[] encode(byte[] bytes){
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        // calculate the length of the resulting output.
        // in general it will be 4/3 the size of the inpu
        // but the input length must be divisible by three.
        // If it isn't the next largest size that is divisible
        // by three is used.
        int mod;
        int length = bytes.length;
        if ((mod = length % 3) != 0){
            length += 3 - mod;
        }
        length = length * 4 / 3;
        ByteArrayOutputStream out = new ByteArrayOutputStream(length);
        try {
            encode(in, out, false);
        } catch (IOException x){
            // This can't happen.
            // The input and output streams were constructed
            // on memory structures that don't actually use IO.
        }
        return out.toByteArray();
    }

    /**
     * Encode data from the InputStream to the OutputStream in Base64.
     *
     * @param in Stream from which to read data that needs to be encoded.
     * @param out Stream to which to write encoded data.
     * @param lineBreaks Whether to insert line breaks every 76 characters in the output.
     * @throws IOException if there is a problem reading or writing.
     */
    public static void encode(InputStream in, OutputStream out, boolean lineBreaks) throws IOException {
        // Base64 encoding converts three bytes of input to
        // four bytes of outpu
        int[] inBuffer = new int[3];
        int lineCount = 0;

        boolean done = false;
        while (!done && (inBuffer[0] = in.read()) != -1){
            // Fill the buffer
            inBuffer[1] = in.read();
            inBuffer[2] = in.read();

            // Calculate the outBuffer
            // The first byte of our in buffer will always be valid
            // but we must check to make sure the other two bytes
            // are not -1 before using them.
            // The basic idea is that the three bytes get split into
            // four bytes along these lines:
            //      [AAAAAABB] [BBBBCCCC] [CCDDDDDD]
            // [xxAAAAAA] [xxBBBBBB] [xxCCCCCC] [xxDDDDDD]
            // bytes are considered to be zero when absent.
            // the four bytes are then mapped to common ASCII symbols

            // A's: first six bits of first byte
            out.write(base64Chars[ inBuffer[0] >> 2 ]);
            if (inBuffer[1] != -1){
                // B's: last two bits of first byte, first four bits of second byte
                out.write(base64Chars [(( inBuffer[0] << 4 ) & 0x30) | (inBuffer[1] >> 4) ]);
                if (inBuffer[2] != -1){
                    // C's: last four bits of second byte, first two bits of third byte
                    out.write(base64Chars [((inBuffer[1] << 2) & 0x3c) | (inBuffer[2] >> 6) ]);
                    // D's: last six bits of third byte
                    out.write(base64Chars [inBuffer[2] & 0x3F]);
                } else {
                    // C's: last four bits of second byte
                    out.write(base64Chars [((inBuffer[1] << 2) & 0x3c)]);
                    // an equals sign for a character that is not a Base64 character
                    out.write('=');
                    done = true;
                }
            } else {
                // B's: last two bits of first byte
                out.write(base64Chars [(( inBuffer[0] << 4 ) & 0x30)]);
                // an equal signs for characters that is not a Base64 characters
                out.write('=');
                out.write('=');
                done = true;
            }
            lineCount += 4;
            if (lineBreaks && lineCount >= 76){
                out.write('\n');
                lineCount = 0;
            }
        }
    }

    /**
     * Decode a Base64 encoded String.
     * Characters that are not part of the Base64 alphabet are ignored
     * in the input.
     * The String is converted to and from bytes according to the platform's
     * default character encoding.
     *
     * @param string The data to decode.
     * @return A decoded String.
     */
    public static String decode(String string){
        return new String(decode(string.getBytes()));
    }

    /**
     * Decode a Base64 encoded String.
     * Characters that are not part of the Base64 alphabet are ignored
     * in the input.
     *
     * @param string The data to decode.
     * @param enc Character encoding to use when converting to and from bytes.
     * @throws UnsupportedEncodingException if the character encoding specified is not supported.
     * @return A decoded String.
     */
    public static String decode(String string, String enc) throws UnsupportedEncodingException {
        return new String(decode(string.getBytes(enc)), enc);
    }

    /**
     * Decode Base64 encoded bytes.
     * Characters that are not part of the Base64 alphabet are ignored
     * in the input.
     *
     * @param bytes The data to decode.
     * @return Decoded bytes.
     */
    public static byte[] decode(byte[] bytes){
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        // calculate the length of the resulting output.
        // in general it will be at most 3/4 the size of the inpu
        // but the input length must be divisible by four.
        // If it isn't the next largest size that is divisible
        // by four is used.
        int mod;
        int length = bytes.length;
        if ((mod = length % 4) != 0){
            length += 4 - mod;
        }
        length = length * 3 / 4;
        ByteArrayOutputStream out = new ByteArrayOutputStream(length);
        try {
            decode(in, out, false);
        } catch (IOException x){
            // This can't happen.
            // The input and output streams were constructed
            // on memory structures that don't actually use IO.
        }
        return out.toByteArray();
    }

    /**
     * Reads the next (decoded) Base64 character from the input stream.
     * Non Base64 characters are skipped.
     *
     * @param in Stream from which bytes are read.
     * @param throwExceptions Throw an exception if an unexpected character
     *    is encountered.
     * @return the next Base64 character from the stream or -1 if
     *    there are no more Base64 characters on the stream.
     * @throws IOException if an IO Error occurs or if an unexpected character
     *    is encountered.
     */
    private static final int readBase64(InputStream in, boolean throwExceptions) throws IOException {
        int read;
        do {
            read = in.read();
            if (read == -1) return -1;
            if (throwExceptions && reverseBase64Chars[(byte)read] == -1 &&
                read != ' ' && read != '\n'  && read != '\r' && read != '\t' && read != '\f' && read != '='){
                throw new IOException ("Unexpected Base64 character: " + read);
            }
            read = reverseBase64Chars[(byte)read];
        } while (read == -1);
        return read;
    }

    /**
     * Decode Base64 encoded data from the InputStream to the OutputStream.
     * Characters in the Base64 alphabet, white space and equals sign are
     * expected to be in urlencoded data.  The presence of other characters
     * could be a sign that the data is corrupted.
     *
     * @param in Stream from which to read data that needs to be decoded.
     * @param out Stream to which to write decoded data.
     * @param throwExceptions Whether to throw exceptions when unexpected data is encountered.
     * @throws IOException if an IO occurs or unexpected data is encountered.
     */
    public static void decode(InputStream in, OutputStream out, boolean throwExceptions) throws IOException {
        // Base64 decoding converts four bytes of input to three bytes of outpu
        int[] inBuffer = new int[4];

        // read bytes unmapping them from their ASCII encoding in the process
        // we must read at least two bytes to be able to output anything
        boolean done = false;
        while (!done && (inBuffer[0] = readBase64(in, throwExceptions)) != -1
            && (inBuffer[1] = readBase64(in, throwExceptions)) != -1){
            // Fill the buffer
            inBuffer[2] = readBase64(in, throwExceptions);
            inBuffer[3] = readBase64(in, throwExceptions);

            // Calculate the output
            // The first two bytes of our in buffer will always be valid
            // but we must check to make sure the other two bytes
            // are not -1 before using them.
            // The basic idea is that the four bytes will get reconstituted
            // into three bytes along these lines:
            // [xxAAAAAA] [xxBBBBBB] [xxCCCCCC] [xxDDDDDD]
            //      [AAAAAABB] [BBBBCCCC] [CCDDDDDD]
            // bytes are considered to be zero when absent.

            // six A and two B
            out.write(inBuffer[0] << 2 | inBuffer[1] >> 4);
            if (inBuffer[2] != -1){
                // four B and four C
                out.write(inBuffer[1] << 4 | inBuffer[2] >> 2);
                if (inBuffer[3] != -1){
                    // two C and six D
                    out.write(inBuffer[2] << 6 | inBuffer[3]);
                } else {
                    done = true;
                }
            } else {
                done = true;
            }
        }
    }
}
