/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nekosarekawaii.vandalism.util;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

public class IPAddressInfo {

    @Getter
    private final String ip;
    @Getter
    private final String rir;

    @SerializedName("is_bogon")
    private final boolean isBogon;

    @SerializedName("is_mobile")
    private final boolean isMobile;

    @SerializedName("is_crawler")
    private final boolean isCrawler;

    @SerializedName("is_datacenter")
    private final boolean isDatacenter;

    @SerializedName("is_tor")
    private final boolean isTor;

    @SerializedName("is_proxy")
    private final boolean isProxy;

    @SerializedName("is_vpn")
    private final boolean isVpn;

    @SerializedName("is_abuser")
    private final boolean isAbuser;

    @Getter
    private final Company company;
    @Getter
    private final Datacenter datacenter;
    @Getter
    private final ASN asn;
    @Getter
    private final Location location;

    @Getter
    @SerializedName("elapsed_ms")
    private final double elapsedMs;

    public IPAddressInfo() {
        this.ip = "";
        this.rir = "";
        this.isBogon = false;
        this.isMobile = false;
        this.isCrawler = false;
        this.isDatacenter = false;
        this.isTor = false;
        this.isProxy = false;
        this.isVpn = false;
        this.isAbuser = false;
        this.company = new Company();
        this.datacenter = new Datacenter();
        this.asn = new ASN();
        this.location = new Location();
        this.elapsedMs = 0.0;
    }

    @Getter
    public static class Company {

        private final String name;

        @SerializedName("abuser_score")
        private final String abuserScore;

        private final String domain;
        private final String type;
        private final String network;
        private final String whois;

        public Company() {
            this.name = "";
            this.abuserScore = "";
            this.domain = "";
            this.type = "";
            this.network = "";
            this.whois = "";
        }

    }

    @Getter
    public static class Datacenter {

        private final String datacenter;
        private final String domain;
        private final String network;

        public Datacenter() {
            this.datacenter = "";
            this.domain = "";
            this.network = "";
        }

    }

    @Getter
    public static class ASN {

        private final int asn;

        @SerializedName("abuser_score")
        private final String abuserScore;

        private final String route;
        private final String descr;
        private final String country;
        private final boolean active;
        private final String org;
        private final String domain;
        private final String abuse;
        private final String type;
        private final String created;
        private final String updated;
        private final String rir;
        private final String whois;

        public ASN() {
            this.asn = 0;
            this.abuserScore = "";
            this.route = "";
            this.descr = "";
            this.country = "";
            this.active = false;
            this.org = "";
            this.domain = "";
            this.abuse = "";
            this.type = "";
            this.created = "";
            this.updated = "";
            this.rir = "";
            this.whois = "";
        }

    }

    public static class Location {

        private final String continent;

        @Getter
        private final String country;

        @Getter
        @SerializedName("country_code")
        private final String countryCode;

        private final String state;
        private final String city;
        private final double latitude;
        private final double longitude;
        private final String zip;
        private final String timezone;

        @SerializedName("local_time")
        private final String localTime;

        @SerializedName("local_time_unix")
        private final long localTimeUnix;

        @SerializedName("is_dst")
        private final boolean isDst;

        public Location() {
            this.continent = "";
            this.country = "";
            this.countryCode = "";
            this.state = "";
            this.city = "";
            this.latitude = 0.0;
            this.longitude = 0.0;
            this.zip = "";
            this.timezone = "";
            this.localTime = "";
            this.localTimeUnix = 0L;
            this.isDst = false;
        }

        public boolean isDst() {
            return isDst;
        }

    }

}
