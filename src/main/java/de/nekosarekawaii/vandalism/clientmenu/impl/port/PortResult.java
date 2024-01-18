/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.clientmenu.impl.port;

import de.florianmichael.rclasses.math.integration.MSTimer;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.clientmenu.impl.widget.ServerInfoWidget;
import net.lenni0451.mcping.MCPing;
import net.lenni0451.mcping.exception.ConnectTimeoutException;
import net.lenni0451.mcping.exception.ConnectionRefusedException;
import net.lenni0451.mcping.exception.DataReadException;
import net.lenni0451.mcping.exception.PacketReadException;
import net.lenni0451.mcping.responses.MCPingResponse;
import net.minecraft.SharedConstants;

import java.net.BindException;
import java.net.UnknownHostException;

public class PortResult {

    private final int port;
    private final String hostname;

    private final ServerInfoWidget serverInfoWidget;

    private PingState currentState, currentQueryState;

    private final MSTimer stateTimer = new MSTimer();

    public PortResult(final int port, final String hostname) {
        this.port = port;
        this.hostname = hostname;
        this.serverInfoWidget = new ServerInfoWidget();
        this.serverInfoWidget.setHostname(hostname);
        this.clear();
    }

    public int getPort() {
        return this.port;
    }

    public String getHostname() {
        return this.hostname;
    }

    public MCPingResponse getMcPingResponse() {
        return this.serverInfoWidget.getMcPingResponse();
    }

    public PingState getCurrentState() {
        return this.currentState;
    }

    public PingState getCurrentQueryState() {
        return this.currentQueryState;
    }

    public void renderTableEntry() {
        if (this.currentState != PingState.WAITING_RESPONSE) {
            if (this.stateTimer.hasReached(15000, true)) {
                this.resetState();
            }
        }
        this.serverInfoWidget.renderTableEntry(false);
    }

    public void renderSubData() {
        this.serverInfoWidget.renderSubData();
    }

    private void resetState() {
        this.currentState = PingState.WAITING_INPUT;
        this.currentQueryState = PingState.WAITING_INPUT;
    }

    private void clear() {
        this.serverInfoWidget.setMcPingResponse(null);
        this.serverInfoWidget.setQueryPingResponse(null);
        this.resetState();
    }

    public void ping() {
        if (!this.hostname.isBlank()) {
            this.clear();
            MCPing.pingModern(SharedConstants.getProtocolVersion())
                    .address(this.hostname, this.port)
                    .timeout(5000, 5000)
                    .exceptionHandler(t -> {
                        if (t instanceof UnknownHostException) {
                            this.currentState = PingState.UNKNOWN_HOST;
                        } else if (t instanceof ConnectionRefusedException) {
                            this.currentState = PingState.CONNECTION_REFUSED;
                        } else if (t instanceof ConnectTimeoutException) {
                            this.currentState = PingState.CONNECTION_TIMED_OUT;
                        } else if (t instanceof DataReadException) {
                            this.currentState = PingState.DATA_READ_FAILED;
                        } else if (t instanceof PacketReadException) {
                            this.currentState = PingState.PACKET_READ_FAILED;
                        } else {
                            this.currentState = PingState.FAILED;
                            Vandalism.getInstance().getLogger().error("Failed to ping " + this.hostname + ":" + this.port, t);
                        }
                    })
                    .finishHandler(response -> {
                        this.serverInfoWidget.setMcPingResponse(response);
                        this.currentState = PingState.SUCCESS;
                    }).getAsync();
            this.currentState = PingState.WAITING_RESPONSE;
            MCPing.pingQuery()
                    .address(this.hostname, this.port)
                    .timeout(5000, 5000)
                    .exceptionHandler(t -> {
                        if (t instanceof BindException) {
                            this.currentQueryState = PingState.BIND_FAILED;
                        } else if (t instanceof UnknownHostException) {
                            this.currentQueryState = PingState.UNKNOWN_HOST;
                        } else if (t instanceof ConnectionRefusedException) {
                            this.currentQueryState = PingState.CONNECTION_REFUSED;
                        } else if (t instanceof ConnectTimeoutException) {
                            this.currentQueryState = PingState.CONNECTION_TIMED_OUT;
                        } else if (t instanceof DataReadException) {
                            this.currentQueryState = PingState.DATA_READ_FAILED;
                        } else if (t instanceof PacketReadException) {
                            this.currentQueryState = PingState.PACKET_READ_FAILED;
                        } else {
                            this.currentQueryState = PingState.FAILED;
                            Vandalism.getInstance().getLogger().error("Failed to ping query " + this.hostname + ":" + this.port, t);
                        }
                    })
                    .finishHandler(response -> {
                        this.serverInfoWidget.setQueryPingResponse(response);
                        this.currentQueryState = PingState.SUCCESS;
                    }).getAsync();
            this.currentQueryState = PingState.WAITING_RESPONSE;
        } else this.currentState = PingState.WAITING_INPUT;
    }

    public enum PingState {

        FAILED("There was an error fetching the server info."),
        BIND_FAILED("Cannot assign requested address."),
        UNKNOWN_HOST("Unknown Host."),
        CONNECTION_REFUSED("Connection Refused."),
        CONNECTION_TIMED_OUT("Connection timed out."),
        DATA_READ_FAILED("Failed to read data."),
        PACKET_READ_FAILED("Failed to read packet."),
        SUCCESS("Successfully fetched the server info."),
        WAITING_RESPONSE("Waiting for response..."),
        WAITING_INPUT("Waiting for input...");

        private final String message;

        PingState(final String message) {
            this.message = message;
        }

        public String getMessage() {
            return this.message;
        }

    }

}
