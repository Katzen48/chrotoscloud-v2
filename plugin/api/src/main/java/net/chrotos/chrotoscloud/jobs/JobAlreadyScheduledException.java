package net.chrotos.chrotoscloud.jobs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class JobAlreadyScheduledException extends RuntimeException {
    private final UUID uniqueId;
}