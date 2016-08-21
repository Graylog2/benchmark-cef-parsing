/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sample;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyBenchmark {

    private static final Pattern HEADER_PATTERN = Pattern.compile("^<\\d+>([a-zA-Z]{3} \\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}) CEF:(\\d+?)\\|(.+?)\\|(.+?)\\|(.+?)\\|(.+?)\\|(.+?)\\|(.+?)\\|(.+?)(?:$|(msg=.+))", Pattern.DOTALL);

    private static final String CEFS[] = {
            "dvc=ip-172-30-2-212 c6a1=fe80::5626:96ff:fed0:943 c6a1Label=TestTest cs2=ip-172-30-2-212->/var/log/auth.log cs2Label=Location msg=Aug 14 14:26:53 ip-172-30-2-212 sshd[16217]: message repeated 2 times",
            "dvc=ip-172-30-2-212 cfp2=90.01 cfp2Label=TestTest cs2=ip-172-30-2-212->/var/log/auth.log cs2Label=Location msg=Aug 14 14:26:53 ip-172-30-2-212 sshd[16217]: message repeated 2 times",
            "dvc=ip-172-30-2-212 cn1=999999999999 cn1Label=TestTest cs2=ip-172-30-2-212->/var/log/auth.log cs2Label=Location msg=Aug 14 14:26:53 ip-172-30-2-212 sshd[16217]: message repeated 2 times",
            "dvc=ip-172-30-2-212 flexNumber2=999999999999 flexNumber2Label=TestTest cs2=ip-172-30-2-212->/var/log/auth.log cs2Label=Location msg=Aug 14 14:26:53 ip-172-30-2-212 sshd[16217]: message repeated 2 times",
            "dvc=ip-172-30-2-212 cs5=Fooo! cs5Label=TestTest cs2=ip-172-30-2-212->/var/log/auth.log cs2Label=Location msg=Aug 14 14:26:53 ip-172-30-2-212 sshd[16217]: message repeated 2 times",
            "dvc=ip-172-30-2-212 flexString1=Fooo! flexString1Label=TestTest cs2=ip-172-30-2-212->/var/log/auth.log cs2Label=Location msg=Aug 14 14:26:53 ip-172-30-2-212 sshd[16217]: message repeated 2 times",
            "dvc=ip-172-30-2-212 deviceCustomDate1=2016-08-19T21:51:08+00:00 deviceCustomDate1Label=TestTest cs2=ip-172-30-2-212->/var/log/auth.log cs2Label=Location msg=Aug 14 14:26:53 ip-172-30-2-212 sshd[16217]: message repeated 2 times",
            "dvc=ip-172-30-2-212 flexDate1=2016-08-19T21:51:08+00:00 flexDate1Label=TestTest cs2=ip-172-30-2-212->/var/log/auth.log cs2Label=Location msg=Aug 14 14:26:53 ip-172-30-2-212 sshd[16217]: message repeated 2 times",
            "dvc=ip-172-30-2-212 cn1=999999999999 cn1Label=TestTest cfp2=90.01 cfp2Label=TestTest2 cs2=ip-172-30-2-212->/var/log/auth.log cs2Label=Location msg=Aug 14 14:26:53 ip-172-30-2-212 sshd[16217]: message repeated 2 times",
            "dvc=ip-172-30-2-212 cs2=ip-172-30-2-212->/var/log/auth.log cs2Label=Location msg=Aug 14 14:26:53 ip-172-30-2-212 sshd[16217]: message repeated 2 times",
            "dvc=ip-172-30-2-212 cnt=3 destinationTranslatedPort=22 deviceDirection=1 dpid=9001 dpt=3342 dvcpid=900 fsize=12 in=543 oldFileSize=1000 sourceTranslatedPort=443 spid=5516 spt=22 type=0 cs2=ip-172-30-2-212->/var/log/auth.log cs2Label=Location msg=Aug 14 14:26:53 ip-172-30-2-212 sshd[16217]: message repeated 2 times",
            "dvc=ip-172-30-2-212 slat=29.7604 slong=95.3698 dlat=53.5511 dlong=9.9937 cs2=ip-172-30-2-212->/var/log/auth.log cs2Label=Location msg=Aug 14 14:26:53 ip-172-30-2-212 sshd[16217]: message repeated 2 times",
            "dvc=ip-172-30-2-212 eventId=9001 cs2=ip-172-30-2-212->/var/log/auth.log cs2Label=Location msg=Aug 14 14:26:53 ip-172-30-2-212 sshd[16217]: message repeated 2 times"
    };

    private static final Pattern KEYVALUE_PATTERN = Pattern.compile("(\\S+)=(\\S+)");
    private static final Splitter.MapSplitter SPLITTER = Splitter.on(CharMatcher.WHITESPACE).withKeyValueSeparator('=');

    @Benchmark
    public void testRegex(Blackhole bh) {
        for (String cef : CEFS) {
            Matcher cefMatcher = HEADER_PATTERN.matcher(cef);
            if (!cefMatcher.find()) {
                return;
            }
            final Matcher m = KEYVALUE_PATTERN.matcher(cefMatcher.group(9));
            if (m.find()) {
                // Parse out all fields into a map.
                ImmutableMap.Builder<String, String> fieldsBuilder = new ImmutableMap.Builder<>();
                while(m.find()) {
                    if (m.groupCount() == 2) {
                        fieldsBuilder.put(m.group(1), m.group(2));
                    }
                }
                bh.consume(fieldsBuilder.build());
            }
        }
    }

    @Benchmark
    public void testSplitter(Blackhole bh) {
        for (String cef : CEFS) {
Matcher cefMatcher = HEADER_PATTERN.matcher(cef);
if (!cefMatcher.find()) {
    return;
}
final Map<String, String> m = SPLITTER.split(cefMatcher.group(9));
if (m.size() > 0) {
    // Parse out all fields into a map.
    ImmutableMap.Builder<String, String> fieldsBuilder = new ImmutableMap.Builder<>();
    fieldsBuilder.putAll(m);
    bh.consume(fieldsBuilder.build());
}
        }
    }

}
