package kangnamUni.TimOp.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLecture is a Querydsl query type for Lecture
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLecture extends EntityPathBase<Lecture> {

    private static final long serialVersionUID = 1424635602L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLecture lecture = new QLecture("lecture");

    public final NumberPath<Integer> credit = createNumber("credit", Integer.class);

    public final StringPath division_class = createString("division_class");

    public final NumberPath<Integer> grade = createNumber("grade", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath liberalArts = createString("liberalArts");

    public final StringPath major = createString("major");

    public final StringPath num = createString("num");

    public final StringPath professor = createString("professor");

    public final StringPath progress_time = createString("progress_time");

    public final StringPath semester = createString("semester");

    public final StringPath syllabus = createString("syllabus");

    public final StringPath time = createString("time");

    public final QTimetable timetable;

    public final StringPath title = createString("title");

    public final NumberPath<Integer> year = createNumber("year", Integer.class);

    public QLecture(String variable) {
        this(Lecture.class, forVariable(variable), INITS);
    }

    public QLecture(Path<? extends Lecture> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLecture(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLecture(PathMetadata metadata, PathInits inits) {
        this(Lecture.class, metadata, inits);
    }

    public QLecture(Class<? extends Lecture> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.timetable = inits.isInitialized("timetable") ? new QTimetable(forProperty("timetable"), inits.get("timetable")) : null;
    }

}

