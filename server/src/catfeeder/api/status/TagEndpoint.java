package catfeeder.api.status;

import catfeeder.api.annotations.Secured;
import catfeeder.api.filters.LoggedInSecurityContext;
import catfeeder.db.DatabaseClient;
import catfeeder.model.Tag;
import catfeeder.model.User;
import catfeeder.model.response.GeneralResponse;
import catfeeder.model.response.status.tag.KnownTagListResponse;
import catfeeder.model.response.status.tag.KnownTagResponse;
import com.j256.ormlite.dao.Dao;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.sql.SQLException;
import java.util.List;

@Secured
@Path("/status/tag")
public class TagEndpoint {

    @Context
    private SecurityContext context;

    @Path("/{tagUID}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public KnownTagResponse getKnownTag(@PathParam("tagUID") long tagUID) throws SQLException {
        User u = ((LoggedInSecurityContext.UserPrincipal)context.getUserPrincipal()).getUser();
        tagUID = tagUID & 0xFFFFFFL;
        Dao<Tag, Integer> knownTagDao = DatabaseClient.getTagDao();
        Tag tag = getTag(knownTagDao, u, tagUID);
        if(tag.getTagUID() > 0) {
            return new KnownTagResponse(tag);
        } else {
            throw new NotFoundException();
        }
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    @POST
    public KnownTagResponse saveOrUpdateTag(@FormParam("tagUID") long tagUID, @FormParam("tagName") String name) throws SQLException {
        User u = ((LoggedInSecurityContext.UserPrincipal)context.getUserPrincipal()).getUser();
        tagUID = tagUID & 0xFFFFFFL;
        Dao<Tag, Integer> knownTagDao = DatabaseClient.getTagDao();
        Tag tag = getTag(knownTagDao, u, tagUID);
        tag.setTagName(name);
        knownTagDao.createOrUpdate(tag);
        return new KnownTagResponse(tag);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list")
    @GET
    public KnownTagListResponse getAllTags() {
        User u = ((LoggedInSecurityContext.UserPrincipal)context.getUserPrincipal()).getUser();
        return new KnownTagListResponse(u.getTags());
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{tagUID}")
    public GeneralResponse deleteTag(@PathParam("tagUID") long tagUID) throws SQLException {
        User u = ((LoggedInSecurityContext.UserPrincipal)context.getUserPrincipal()).getUser();
        Dao<Tag, Integer> knownTagDao = DatabaseClient.getTagDao();
        Tag tag = getTag(knownTagDao, u, tagUID);
        return new GeneralResponse(knownTagDao.delete(tag) > 0);
    }

    private static Tag getTag(Dao<Tag, Integer> knownTagDao, User u, long tagUID) throws SQLException {
        tagUID = tagUID & 0xFFFFFFL;
        Tag queryTag = new Tag();
        queryTag.setTagUID(tagUID);
        queryTag.setUser(u);
        List<Tag> tags = knownTagDao.queryForMatching(queryTag);
        return (tags == null || tags.size() == 0) ? queryTag : tags.get(0);
    }

}
