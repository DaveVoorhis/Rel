package org.reldb.rel.v0.version;

/**
 * Version, copyright, and license information.
 *
 * @author  dave
 */
public class Version {
   
    private static final double PRODUCT_VERSION = 3.016;
    private static final int DATABASE_VERSION = 0;

	public final static String expectedBerkeleyDBVersion = "18.3.12";
    
    /** No instances. */
    private Version() {}
    
    public static double getProductVersion() {
        return PRODUCT_VERSION;
    }
    
    /** Get database version number. Changes to this mean databases are not compatible. */
    public static int getDatabaseFormatVersion() {
        return DATABASE_VERSION;
    }

    /** Name of the jar file of a particular version. */
    public static String getCoreJarFilename(int dbVersion) {
		return String.format("rel%04d.jar", dbVersion);    	
    }
    
    /** Name of the jar file that contains this class. */
	public static String getCoreJarFilename() {
		return getCoreJarFilename(getDatabaseFormatVersion());
	}

	/** Name of the jar file that contains the Berkeley Java DB. */
	public static String getBerkeleyDbJarFilename() {
		return "je-" + expectedBerkeleyDBVersion + ".jar";
	}

    /** Get version string. */
    public static String getVersion() {
        return String.valueOf(getProductVersion());
    }
    
    /** Get copyright string. */
    public static String getCopyright() {
        return "Rel DBMS version " + getVersion() + "\n" +
            "Copyright © 2004 - 2022 Dave Voorhis\n" +
            "All Rights Reserved\n" +
            "For further information, please see https://reldb.org";
    }
    
    /** Get in-software license information. */
    public static String getLicense() {
        return "Rel comes with ABSOLUTELY NO WARRANTY; for details, type:\n" +
               "\twarranty FROM TUPLE FROM sys.Version\n" + 
               "This is free software, and you are welcome to redistribute it\n" + 
               "under certain conditions.\n" +
               "For details, type:\n" + 
               "\tredistribution FROM TUPLE FROM sys.Version\n" +
        	   "To view the catalog, type:\n" + 
        	   "\tsys.Catalog";
    }
    
    /** Get in-software warranty information. */
    public static String getWarranty() {
        return "BECAUSE REL IS LICENSED FREE OF CHARGE," + "\n" +
               "THERE IS NO WARRANTY FOR REL, TO THE EXTENT" + "\n" +
               "PERMITTED BY APPLICABLE LAW. EXCEPT WHEN OTHERWISE" + "\n" +
               "STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER" + "\n" +
               "PARTIES PROVIDE REL 'AS IS' WITHOUT WARRANTY" + "\n" +
               "OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING," + "\n" +
               "BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF" + "\n" +
               "MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE." + "\n" +
               "THE ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE" + "\n" +
               "PROGRAM IS WITH YOU. SHOULD REL PROVE DEFECTIVE," + "\n" +
               "YOU ASSUME THE COST OF ALL NECESSARY SERVICING," + "\n" +
               "REPAIR OR CORRECTION." + "\n" + "\n" +
               "IN NO EVENT UNLESS REQUIRED BY APPLICABLE LAW OR" + "\n" +
               "AGREED TO IN WRITING WILL ANY COPYRIGHT HOLDER," + "\n" +
               "OR ANY OTHER PARTY WHO MAY MODIFY AND/OR REDISTRIBUTE" + "\n" +
               "REL AS PERMITTED ABOVE, BE LIABLE TO YOU FOR" + "\n" +
               "DAMAGES, INCLUDING ANY GENERAL, SPECIAL, INCIDENTAL" + "\n" +
               "OR CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE" + "\n" +
               "OR INABILITY TO USE REL (INCLUDING BUT NOT" + "\n" +
               "LIMITED TO LOSS OF DATA OR DATA BEING RENDERED INACCURATE" + "\n" +
               "OR LOSSES SUSTAINED BY YOU OR THIRD PARTIES OR A FAILURE" + "\n" +
               "OF REL TO OPERATE WITH ANY OTHER PROGRAMS)," + "\n" +
               "EVEN IF SUCH HOLDER OR OTHER PARTY HAS BEEN ADVISED" + "\n" +
               "OF THE POSSIBILITY OF SUCH DAMAGES.";
    }
    
    /** Get in-software redistribution information. */
    public static String getRedistribution() {
    	return
	    	"TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION\n" +
	    	"\n" +
	    	"1. Definitions.\n" +
	    	"\n" +
	    	"\"License\" shall mean the terms and conditions for use, reproduction,\n" +
	    	"and distribution as defined by Sections 1 through 9 of this document.\n" +
	    	"\n" +
	    	"\"Licensor\" shall mean the copyright owner or entity authorized by the\n" +
	    	"copyright owner that is granting the License.\n" +
	    	"\n" +
	    	"\"Legal Entity\" shall mean the union of the acting entity and all other\n" +
	    	"entities that control, are controlled by, or are under common control\n" +
	    	"with that entity. For the purposes of this definition, \"control\" means\n" +
	    	"(i) the power, direct or indirect, to cause the direction or\n" +
	    	"management of such entity, whether by contract or otherwise, or (ii)\n" +
	    	"ownership of fifty percent (50%) or more of the outstanding shares, or\n" +
	    	"(iii) beneficial ownership of such entity.\n" +
	    	"\n" +
	    	"\"You\" (or \"Your\") shall mean an individual or Legal Entity exercising\n" +
	    	"permissions granted by this License.\n" +
	    	"\n" +
	    	"\"Source\" form shall mean the preferred form for making modifications,\n" +
	    	"including but not limited to software source code, documentation\n" +
	    	"source, and configuration files.\n" +
	    	"\n" +
	    	"\"Object\" form shall mean any form resulting from mechanical\n" +
	    	"transformation or translation of a Source form, including but not\n" +
	    	"limited to compiled object code, generated documentation, and\n" +
	    	"conversions to other media types.\n" +
	    	"\n" +
	    	"\"Work\" shall mean the work of authorship, whether in Source or Object\n" +
	    	"form, made available under the License, as indicated by a copyright\n" +
	    	"notice that is included in or attached to the work.\n" +
	    	"\n" +
	    	"\"Derivative Works\" shall mean any work, whether in Source or Object\n" +
	    	"form, that is based on (or derived from) the Work and for which the\n" +
	    	"editorial revisions, annotations, elaborations, or other modifications\n" +
	    	"represent, as a whole, an original work of authorship. For the\n" +
	    	"purposes of this License, Derivative Works shall not include works\n" +
	    	"that remain separable from, or merely link (or bind by name) to the\n" +
	    	"interfaces of, the Work and Derivative Works thereof.\n" +
	    	"\n" +
	    	"\"Contribution\" shall mean any work of authorship, including the\n" +
	    	"original version of the Work and any modifications or additions to\n" +
	    	"that Work or Derivative Works thereof, that is intentionally submitted\n" +
	    	"to Licensor for inclusion in the Work by the copyright owner or by an\n" +
	    	"individual or Legal Entity authorized to submit on behalf of the\n" +
	    	"copyright owner. For the purposes of this definition, \"submitted\"\n" +
	    	"means any form of electronic, verbal, or written communication sent to\n" +
	    	"the Licensor or its representatives, including but not limited to\n" +
	    	"communication on electronic mailing lists, source code control\n" +
	    	"systems, and issue tracking systems that are managed by, or on behalf\n" +
	    	"of, the Licensor for the purpose of discussing and improving the Work,\n" +
	    	"but excluding communication that is conspicuously marked or otherwise\n" +
	    	"designated in writing by the copyright owner as \"Not a Contribution.\"\n" +
	    	"\n" +
	    	"\"Contributor\" shall mean Licensor and any individual or Legal Entity\n" +
	    	"on behalf of whom a Contribution has been received by Licensor and\n" +
	    	"subsequently incorporated within the Work.\n" +
	    	"\n" +
	    	"2. Grant of Copyright License. \n" +
	    	"\n" +
	    	"Subject to the terms and conditions of this License, each Contributor\n" +
	    	"hereby grants to You a perpetual, worldwide, non-exclusive, no-charge,\n" +
	    	"royalty-free, irrevocable copyright license to reproduce, prepare\n" +
	    	"Derivative Works of, publicly display, publicly perform, sublicense,\n" +
	    	"and distribute the Work and such Derivative Works in Source or Object\n" +
	    	"form.\n" +
	    	"\n" +
	    	"3. Grant of Patent License. \n" +
	    	"\n" +
	    	"Subject to the terms and conditions of this License, each Contributor\n" +
	    	"hereby grants to You a perpetual, worldwide, non-exclusive, no-charge,\n" +
	    	"royalty-free, irrevocable (except as stated in this section) patent\n" +
	    	"license to make, have made, use, offer to sell, sell, import, and\n" +
	    	"otherwise transfer the Work, where such license applies only to those\n" +
	    	"patent claims licensable by such Contributor that are necessarily\n" +
	    	"infringed by their Contribution(s) alone or by combination of their\n" +
	    	"Contribution(s) with the Work to which such Contribution(s) was\n" +
	    	"submitted. If You institute patent litigation against any entity\n" +
	    	"(including a cross-claim or counterclaim in a lawsuit) alleging that\n" +
	    	"the Work or a Contribution incorporated within the Work constitutes\n" +
	    	"direct or contributory patent infringement, then any patent licenses\n" +
	    	"granted to You under this License for that Work shall terminate as of\n" +
	    	"the date such litigation is filed.\n" +
	    	"\n" +
	    	"4. Redistribution. \n" +
	    	"\n" +
	    	"You may reproduce and distribute copies of the Work or Derivative\n" +
	    	"Works thereof in any medium, with or without modifications, and in\n" +
	    	"Source or Object form, provided that You meet the following\n" +
	    	"conditions:\n" +
	    	"\n" +
	    	"(a) You must give any other recipients of the Work or\n" +
	    	"    Derivative Works a copy of this License; and\n" +
	    	"\n" +
	    	"(b) You must cause any modified files to carry prominent notices\n" +
	    	"    stating that You changed the files; and\n" +
	    	"\n" +
	    	"(c) You must retain, in the Source form of any Derivative Works\n" +
	    	"    that You distribute, all copyright, patent, trademark, and\n" +
	    	"    attribution notices from the Source form of the Work,\n" +
	    	"    excluding those notices that do not pertain to any part of\n" +
	    	"    the Derivative Works; and within a display generated by the\n" +
	    	"    Derivative Works, if and wherever such third-party notices\n" +
	    	"    normally appear. The contents of the NOTICE file are for\n" +
	    	"    informational purposes only and do not modify the License.\n" + 
	    	"    You may add Your own attribution notices within Derivative\n" + 
	    	"    Works that You distribute, alongside or as an addendum to the\n" +
	    	"    NOTICE text from the Work, provided that such additional\n" +
	    	"    attribution notices cannot be construed as modifying the License.\n" +
	    	"\n" +
	    	"You may add Your own copyright statement to Your modifications and may\n" +
	    	"provide additional or different license terms and conditions for use,\n" +
	    	"reproduction, or distribution of Your modifications, or for any such\n" +
	    	"Derivative Works as a whole, provided Your use, reproduction, and\n" +
	    	"distribution of the Work otherwise complies with the conditions stated\n" +
	    	"in this License.\n" +
	    	"\n" +
	    	"5. Submission of Contributions. \n" +
	    	"\n" +
	    	"Unless You explicitly state otherwise, any Contribution intentionally\n" +
	    	"submitted for inclusion in the Work by You to the Licensor shall be\n" +
	    	"under the terms and conditions of this License, without any additional\n" +
	    	"terms or conditions.  Notwithstanding the above, nothing herein shall\n" +
	    	"supersede or modify the terms of any separate license agreement you\n" +
	    	"may have executed with Licensor regarding such Contributions.\n" +
	    	"\n" +
	    	"6. Trademarks. \n" +
	    	"\n" +
	    	"This License does not grant permission to use the trade names,\n" +
	    	"trademarks, service marks, or product names of the Licensor, except as\n" +
	    	"required for reasonable and customary use in describing the origin of\n" +
	    	"the Work and reproducing the content of the NOTICE file.\n" +
	    	"\n" +
	    	"7. Disclaimer of Warranty. \n" +
	    	"\n" +
	    	"Unless required by applicable law or agreed to in writing, Licensor\n" +
	    	"provides the Work (and each Contributor provides its Contributions) on\n" +
	    	"an \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either\n" +
	    	"express or implied, including, without limitation, any warranties or\n" +
	    	"conditions of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR\n" +
	    	"A PARTICULAR PURPOSE. You are solely responsible for determining the\n" +
	    	"appropriateness of using or redistributing the Work and assume any\n" +
	    	"risks associated with Your exercise of permissions under this License.\n" +
	    	"\n" +
	    	"8. Limitation of Liability. \n" +
	    	"\n" +
	    	"In no event and under no legal theory, whether in tort (including\n" +
	    	"negligence), contract, or otherwise, unless required by applicable law\n" +
	    	"(such as deliberate and grossly negligent acts) or agreed to in\n" +
	    	"writing, shall any Contributor be liable to You for damages, including\n" +
	    	"any direct, indirect, special, incidental, or consequential damages of\n" +
	    	"any character arising as a result of this License or out of the use or\n" +
	    	"inability to use the Work (including but not limited to damages for\n" +
	    	"loss of goodwill, work stoppage, computer failure or malfunction, or\n" +
	    	"any and all other commercial damages or losses), even if such\n" +
	    	"Contributor has been advised of the possibility of such damages.\n" +
	    	"\n" +
	    	"9. Accepting Warranty or Additional Liability. \n" +
	    	"\n" +
	    	"While redistributing the Work or Derivative Works thereof, You may\n" +
	    	"choose to offer, and charge a fee for, acceptance of support,\n" +
	    	"warranty, indemnity, or other liability obligations and/or rights\n" +
	    	"consistent with this License. However, in accepting such obligations,\n" +
	    	"You may act only on Your own behalf and on Your sole responsibility,\n" +
	    	"not on behalf of any other Contributor, and only if You agree to\n" +
	    	"indemnify, defend, and hold each Contributor harmless for any\n" +
	    	"liability incurred by, or claims asserted against, such Contributor by\n" +
	    	"reason of your accepting any such warranty or additional liability.\n" +
	    	"\n" +
	    	"END OF TERMS AND CONDITIONS";    	
    }
}
