package com.xavierpandis.com.web.rest.mapper;

import com.xavierpandis.com.domain.*;
import com.xavierpandis.com.web.rest.dto.TrackDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Track and its DTO TrackDTO.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, })
public interface TrackMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.login", target = "userLogin")
    TrackDTO trackToTrackDTO(Track track);

    List<TrackDTO> tracksToTrackDTOs(List<Track> tracks);

    @Mapping(source = "userId", target = "user")
    Track trackDTOToTrack(TrackDTO trackDTO);

    List<Track> trackDTOsToTracks(List<TrackDTO> trackDTOs);
}
