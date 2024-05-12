/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.clientwindow.impl.port;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.clientwindow.impl.widget.ServerInfoWidget;
import de.nekosarekawaii.vandalism.util.common.MSTimer;
import lombok.Getter;
import net.lenni0451.mcping.MCPing;
import net.lenni0451.mcping.exception.ConnectTimeoutException;
import net.lenni0451.mcping.exception.ConnectionRefusedException;
import net.lenni0451.mcping.exception.DataReadException;
import net.lenni0451.mcping.exception.PacketReadException;
import net.minecraft.SharedConstants;

import java.net.UnknownHostException;

public class PortResult {

    @Getter
    private final int port;

    @Getter
    private final String hostname;

    private final ServerInfoWidget serverInfoWidget;

    @Getter
    private PingState currentState;

    private final MSTimer stateTimer = new MSTimer();

    public PortResult(final int port, final String address) {
        this.port = port;
        this.hostname = address;
        this.serverInfoWidget = new ServerInfoWidget();
        this.serverInfoWidget.setAddress(address);
        this.clear();
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
    }

    private void clear() {
        this.serverInfoWidget.setMcPingResponse(null);
        this.resetState();
    }

    public void ping(final Runnable onFinished) {
        if (!this.hostname.isBlank()) {
            this.clear();
            MCPing.pingModern(SharedConstants.getProtocolVersion())
                    .address(this.hostname, this.port)
                    .timeout(5000, 5000)
                    .exceptionHandler(t -> {
                        switch (t) {
                            case UnknownHostException unknownHostException ->
                                    this.currentState = PingState.UNKNOWN_HOST;
                            case ConnectionRefusedException connectionRefusedException ->
                                    this.currentState = PingState.CONNECTION_REFUSED;
                            case ConnectTimeoutException connectTimeoutException ->
                                    this.currentState = PingState.CONNECTION_TIMED_OUT;
                            case DataReadException dataReadException -> this.currentState = PingState.DATA_READ_FAILED;
                            case PacketReadException packetReadException ->
                                    this.currentState = PingState.PACKET_READ_FAILED;
                            case null, default -> {
                                this.currentState = PingState.FAILED;
                                Vandalism.getInstance().getLogger().error("Failed to ping {}:{}", this.hostname, this.port, t);
                            }
                        }
                    })
                    .finishHandler(response -> {
                        this.serverInfoWidget.setMcPingResponse(response);
                        this.currentState = PingState.SUCCESS;
                        onFinished.run();
                    }).getSync();
            this.currentState = PingState.WAITING_RESPONSE;
        } else this.currentState = PingState.WAITING_INPUT;
    }

    @Getter
    public enum PingState {

        FAILED("There was an error fetching the server info."),
        BIND_FAILED("Cannot assign requested address."),
        UNKNOWN_HOST("Unknown host."),
        CONNECTION_REFUSED("Connection refused."),
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

    }

}
