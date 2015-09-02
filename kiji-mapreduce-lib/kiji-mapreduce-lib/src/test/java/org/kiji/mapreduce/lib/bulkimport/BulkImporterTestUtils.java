/**
 * (c) Copyright 2013 WibiData, Inc.
 *
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kiji.mapreduce.lib.bulkimport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Set;

import com.google.common.collect.Sets;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.kiji.schema.EntityId;
import org.kiji.schema.KijiRowData;

/**
 * Utility class containing constants for resources and validation methods for bulk importer tests.
 */
public final class BulkImporterTestUtils {
  private static final Logger LOG = LoggerFactory.getLogger(BulkImporterTestUtils.class);

  public static final String CSV_IMPORT_DATA =
      "org/kiji/mapreduce/lib/mapping/TestCSVImportInput.txt";

  public static final String TIMESTAMP_CSV_IMPORT_DATA =
      "org/kiji/mapreduce/lib/mapping/TimestampCSVImportInput.txt";

  public static final String TSV_IMPORT_DATA =
      "org/kiji/mapreduce/lib/mapping/TestTSVImportInput.txt";

  public static final String JSON_IMPORT_DATA =
      "org/kiji/mapreduce/lib/mapping/TestJSONImportInput.txt";

  public static final String XML_IMPORT_DATA =
      "org/kiji/mapreduce/lib/mapping/TestXMLImportInput.txt";

  public static final String COMPLEX_JSON_IMPORT_DATA =
      "org/kiji/mapreduce/lib/mapping/TestComplexJSONImportInput.txt";

  public static final String HEADERLESS_CSV_IMPORT_DATA =
      "org/kiji/mapreduce/lib/mapping/HeaderlessCSVImportInput.txt";

  public static final String PRIMITIVE_IMPORT_DATA =
      "org/kiji/mapreduce/lib/mapping/TestPrimitiveImportInput.txt";

  public static final String LOCAL_RESOURCE_PREFIX = "src/test/resources/";

  public static final String FOO_IMPORT_DESCRIPTOR =
    "org/kiji/mapreduce/lib/mapping/foo-test-import-descriptor.json";

  public static final String FOO_TIMESTAMP_IMPORT_DESCRIPTOR =
      "org/kiji/mapreduce/lib/mapping/foo-test-timestamp-import-descriptor.json";

  public static final String FOO_TIMESTAMP_XML_IMPORT_DESCRIPTOR =
      "org/kiji/mapreduce/lib/mapping/foo-test-timestamp-xml-import-descriptor.json";

  public static final String FOO_JSONPATH_IMPORT_DESCRIPTOR =
      "org/kiji/mapreduce/lib/mapping/foo-test-jsonpath-import-descriptor.json";

  public static final String FOO_XML_IMPORT_DESCRIPTOR =
      "org/kiji/mapreduce/lib/mapping/foo-test-xml-import-descriptor.json";

  public static final String FOO_LG_XML_IMPORT_DESCRIPTOR =
      "org/kiji/mapreduce/lib/mapping/foo-test-lg-xml-import-descriptor.json";

  public static final String FOO_PRIMITIVE_IMPORT_DESCRIPTOR =
      "org/kiji/mapreduce/lib/mapping/foo-test-primitive-import-descriptor.json";

  public static final String FOO_INVALID_DESCRIPTOR =
      "org/kiji/mapreduce/lib/mapping/foo-test-invalid.json";

  public static final String COMMON_LOG_LAYOUT =
      "org/kiji/mapreduce/lib/layout/commonlog-layout.json";

  public static final String COMMON_LOG_IMPORT_DATA =
      "org/kiji/mapreduce/lib/mapping/TestCommonLog.log";

  public static final String COMMON_LOG_IMPORT_DESCRIPTOR =
      "src/main/resources/org/kiji/mapreduce/lib/mapping/commonlog-import-descriptor.json";

  public static String localResource(String resource) {
    return LOCAL_RESOURCE_PREFIX + resource;
  }

  /** No constructor for this utility class. */
  private BulkImporterTestUtils() {}

  /**
   * Validates that the imported rows match up with the expected results.  The imported KijiRowData
   * can come from a KijiRowScanner.
   *
   * @param importedKijiRowData an iterable of KijiRowData whose contents to validate.
   * @param validateTimestamps whether we should verify that the timestamps are as expected.
   * @throws IOException if there's an error reading the row data
   */
  public static void validateImportedRows(Iterable<KijiRowData> importedKijiRowData,
      boolean validateTimestamps)
      throws IOException {
    long rowsProcessed = 0;
    for (KijiRowData row : importedKijiRowData) {
      final EntityId eid = row.getEntityId();
      final String rowId = Bytes.toString((byte[]) eid.getComponentByIndex(0));

      // Validate that this row matches the data
      if (rowId.equals("Bob")) {
        assertEquals("Bob", row.getMostRecentValue("info", "first_name").toString());
        assertEquals("Jones", row.getMostRecentValue("info", "last_name").toString());
        assertEquals("bobbyj@aol.com", row.getMostRecentValue("info", "email").toString());
        assertEquals("415-555-4161", row.getMostRecentValue("info", "phone").toString());
        if (validateTimestamps) {
          // Get all the timestamp for each cell in the row.
          Set<Long> rowTimestamps = Sets.newHashSet();
          for (String qualifier : row.getQualifiers("info")) {
            rowTimestamps.addAll(row.getTimestamps("info", qualifier));
          }
          assertEquals(1, rowTimestamps.size());
          assertTrue(rowTimestamps.contains(1361520000L));
        }
      } else if (rowId.equals("Alice")) {
        assertEquals("Alice", row.getMostRecentValue("info", "first_name").toString());
        assertEquals("Smith", row.getMostRecentValue("info", "last_name").toString());
        assertEquals("alice.smith@yahoo.com", row.getMostRecentValue("info", "email").toString());
        assertNull(row.getMostRecentValue("info", "phone"));
        if (validateTimestamps) {
          // Get all the timestamp for each cell in the row.
          Set<Long> rowTimestamps = Sets.newHashSet();
          for (String qualifier : row.getQualifiers("info")) {
            rowTimestamps.addAll(row.getTimestamps("info", qualifier));
          }
          assertEquals(1, rowTimestamps.size());
          assertTrue(rowTimestamps.contains(1352880000L));
        }
      } else if (rowId.equals("John")) {
        assertEquals("John", row.getMostRecentValue("info", "first_name").toString());
        assertEquals("Doe", row.getMostRecentValue("info", "last_name").toString());
        assertEquals("johndoe@gmail.com", row.getMostRecentValue("info", "email").toString());
        assertEquals("202-555-9876", row.getMostRecentValue("info", "phone").toString());
        if (validateTimestamps) {
          // Get all the timestamp for each cell in the row.
          Set<Long> rowTimestamps = Sets.newHashSet();
          for (String qualifier : row.getQualifiers("info")) {
            rowTimestamps.addAll(row.getTimestamps("info", qualifier));
          }
          assertEquals(1, rowTimestamps.size());
          assertTrue(rowTimestamps.contains(1284102000L));
        }
      } else {
        fail("Found an unexpected row: " + rowId);
      }
      rowsProcessed++;
    }
    assertEquals("Rows processed ", 3L, rowsProcessed);
  }
}
