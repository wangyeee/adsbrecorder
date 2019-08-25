<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="2.0" xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" version="1.0" indent="yes"/>
  <xsl:attribute-set name="header_cell">
    <xsl:attribute name="border">solid 0.2mm black</xsl:attribute>
    <xsl:attribute name="text-align">center</xsl:attribute>
    <xsl:attribute name="font-size">12pt</xsl:attribute>
  </xsl:attribute-set>
  <xsl:attribute-set name="data_cell">
    <xsl:attribute name="border">solid 0.2mm black</xsl:attribute>
    <xsl:attribute name="font-size">10pt</xsl:attribute>
    <xsl:attribute name="padding-left">2pt</xsl:attribute>
  </xsl:attribute-set>
  <xsl:template match="SIMPLE_DAILY_SUMMARY_REPORT">
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
      <fo:layout-master-set>
        <fo:simple-page-master page-height="297mm" page-width="210mm"
                               margin="5mm 25mm 5mm 25mm" master-name="PageMaster">
          <fo:region-body margin="20mm 0mm 20mm 0mm"/>
        </fo:simple-page-master>
      </fo:layout-master-set>
      <fo:page-sequence master-reference="PageMaster">
        <fo:flow flow-name="xsl-region-body" >
          <fo:block>
            <xsl:apply-templates select="./records"/>
          </fo:block>
        </fo:flow>
      </fo:page-sequence>
    </fo:root>
  </xsl:template>
  <xsl:template match="records">
    <!-- <fo:table table-layout="fixed" width="100%">
      <fo:table-body>
      </fo:table-body>
    </fo:table> -->
    <xsl:for-each-group select="./TRACKING_RECORD" group-by="./ICAO">
      <fo:table table-layout="fixed" width="100%">
        <fo:table-body>
          <fo:table-row>
            <fo:table-cell xsl:use-attribute-sets="header_cell">
              <fo:block>Flight</fo:block>
            </fo:table-cell>
            <fo:table-cell xsl:use-attribute-sets="header_cell">
              <fo:block>ICAO Address</fo:block>
            </fo:table-cell>
            <fo:table-cell xsl:use-attribute-sets="header_cell">
              <fo:block>Latitude</fo:block>
            </fo:table-cell>
            <fo:table-cell xsl:use-attribute-sets="header_cell">
              <fo:block>Longitude</fo:block>
            </fo:table-cell>
            <fo:table-cell xsl:use-attribute-sets="header_cell">
              <fo:block>Altitude</fo:block>
            </fo:table-cell>
          </fo:table-row>
          <xsl:for-each select="current-group()">
            <xsl:sort select="./lastTimeSeen" order="ascending"/>
            <fo:table-row>
              <fo:table-cell xsl:use-attribute-sets="data_cell">
                <fo:block>
                  <xsl:value-of select="./flight"/>
                </fo:block>
              </fo:table-cell>
              <fo:table-cell xsl:use-attribute-sets="data_cell">
                <fo:block>
                  <xsl:value-of select="./ICAO"/>
                </fo:block>
              </fo:table-cell>
              <fo:table-cell xsl:use-attribute-sets="data_cell">
                <fo:block>
                  <xsl:value-of select="./latitude"/>
                </fo:block>
              </fo:table-cell>
              <fo:table-cell xsl:use-attribute-sets="data_cell">
                <fo:block>
                  <xsl:value-of select="./longitude"/>
                </fo:block>
              </fo:table-cell>
              <fo:table-cell xsl:use-attribute-sets="data_cell">
                <fo:block>
                  <xsl:value-of select="./altitude"/>
                </fo:block>
              </fo:table-cell>
            </fo:table-row>
          </xsl:for-each>
        </fo:table-body>
      </fo:table>
      <xsl:if test="position() != last()">
        <fo:block page-break-before="always"/>
      </xsl:if>
    </xsl:for-each-group>
  </xsl:template>
</xsl:stylesheet>
