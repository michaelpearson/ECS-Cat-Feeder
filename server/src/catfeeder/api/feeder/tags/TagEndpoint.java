package catfeeder.api.feeder.tags;

import catfeeder.api.annotations.Secured;
import catfeeder.api.filters.LoggedInSecurityContext;
import catfeeder.db.DatabaseClient;
import catfeeder.exceptions.FeederNotConnected;
import catfeeder.mappers.CardInfoToTagResponseMapper;
import catfeeder.model.*;
import catfeeder.model.response.GeneralResponse;
import catfeeder.model.response.catfeeder.tag.ReadCardResponse;
import catfeeder.model.response.status.tag.KnownTagListResponse;
import catfeeder.model.response.status.tag.KnownTagResponse;
import com.j256.ormlite.dao.Dao;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.sql.SQLException;

@Secured
@Path("/feeder/{feederId}/tag")
public class TagEndpoint {

    @Context
    private SecurityContext context;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/lastTag")
    public ReadCardResponse getAvailableTag(@PathParam("feederId") int feederId) throws SQLException, InterruptedException {
        User user = ((LoggedInSecurityContext.UserPrincipal)context.getUserPrincipal()).getUser();
        CatFeeder feeder = DatabaseClient.getFeederDao().queryForId(feederId);
        if(!user.doesUserOwnCatfeeder(feeder)) {
            throw new NotFoundException();
        }
        CardInfo info;
        try {
            info = feeder.getLastCardInfo();
        } catch (FeederNotConnected ignore) {
            throw new ServiceUnavailableException();
        }

        if(info == null) {
            return new ReadCardResponse();
        }
        return CardInfoToTagResponseMapper.mapCardToTagResponse(info);
    }


    @Path("/{tagUID}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public KnownTagResponse getKnownTag(@PathParam("feederId") int feederId,
                                        @PathParam("tagUID") long tagUID) throws SQLException {
        User u = ((LoggedInSecurityContext.UserPrincipal)context.getUserPrincipal()).getUser();
        CatFeeder feeder = DatabaseClient.getFeederDao().queryForId(feederId);
        if(!u.doesUserOwnCatfeeder(feeder)) {
            throw new NotFoundException();
        }
        Tag t = feeder.getTags().stream().filter(tag -> tag.getTagUID() == tagUID).findFirst().orElse(null);
        if(t == null) {
            throw new NotFoundException();
        }
        return new KnownTagResponse(t);

    }

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public KnownTagResponse saveOrUpdateTag(@PathParam("feederId") int feederId,
                                            @FormParam("tagUID") long tagUID,
                                            @FormParam("tagName") String name) throws SQLException {
        User u = ((LoggedInSecurityContext.UserPrincipal)context.getUserPrincipal()).getUser();
        CatFeeder feeder = DatabaseClient.getFeederDao().queryForId(feederId);

        if(feeder == null || !u.doesUserOwnCatfeeder(feeder)) {
            throw new NotFoundException();
        }

        Dao<Tag, Integer> knownTagDao = DatabaseClient.getTagDao();

        Tag t = feeder.getTags().stream().filter(tag -> tag.getTagUID() == tagUID).findFirst().orElse(null);
        if(t == null) {
            t = new Tag();
        }
        t.setFeeder(feeder);
        t.setTagUID(tagUID);
        t.setTagName(name);

        knownTagDao.createOrUpdate(t);
        return new KnownTagResponse(t);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list")
    @GET
    public KnownTagListResponse getAllTags(@PathParam("feederId") int feederId) throws SQLException {
        User u = ((LoggedInSecurityContext.UserPrincipal)context.getUserPrincipal()).getUser();
        CatFeeder feeder = DatabaseClient.getFeederDao().queryForId(feederId);
        if(feeder == null || !u.doesUserOwnCatfeeder(feeder)) {
            throw new NotFoundException();
        }
        return new KnownTagListResponse(feeder.getTags());
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{tagId}")
    public GeneralResponse deleteTag(@PathParam("feederId") int feederId,
                                     @PathParam("tagId") int tagId) throws SQLException {
        User u = ((LoggedInSecurityContext.UserPrincipal)context.getUserPrincipal()).getUser();
        CatFeeder feeder = DatabaseClient.getFeederDao().queryForId(feederId);
        if(feeder == null || !u.doesUserOwnCatfeeder(feeder)) {
            throw new NotFoundException();
        }
        Dao<Tag, Integer> knownTagDao = DatabaseClient.getTagDao();
        Tag t = feeder.getTags().stream().filter(tag -> tag.getId() == tagId).findFirst().orElse(null);
        if(t == null) {
            throw new NotFoundException();
        }
        return new GeneralResponse(knownTagDao.delete(t) > 0);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/setTrusted")
    public GeneralResponse setTrustedTag(@PathParam("feederId") int feederId,
                                         @FormParam("tagId") int tagId) throws SQLException {
        User u = ((LoggedInSecurityContext.UserPrincipal)context.getUserPrincipal()).getUser();
        CatFeeder feeder = DatabaseClient.getFeederDao().queryForId(feederId);
        if(feeder == null || !u.doesUserOwnCatfeeder(feeder)) {
            throw new NotFoundException();
        }
        Tag t = feeder.getTags().stream().filter(tag -> tag.getId() == tagId).findFirst().orElse(null);
        if(t == null) {
            throw new NotFoundException();
        }
        feeder.setTrustedTag(t);
        DatabaseClient.getFeederDao().update(feeder);
        return new GeneralResponse(true);
    }
}
