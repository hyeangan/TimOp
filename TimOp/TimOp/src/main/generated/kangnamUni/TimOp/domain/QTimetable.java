package kangnamUni.TimOp.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTimetable is a Querydsl query type for Timetable
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTimetable extends EntityPathBase<Timetable> {

    private static final long serialVersionUID = -63978123L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTimetable timetable = new QTimetable("timetable");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<Lecture, QLecture> lectures = this.<Lecture, QLecture>createList("lectures", Lecture.class, QLecture.class, PathInits.DIRECT2);

    public final QMember member;

    public final StringPath name = createString("name");

    public QTimetable(String variable) {
        this(Timetable.class, forVariable(variable), INITS);
    }

    public QTimetable(Path<? extends Timetable> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTimetable(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTimetable(PathMetadata metadata, PathInits inits) {
        this(Timetable.class, metadata, inits);
    }

    public QTimetable(Class<? extends Timetable> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

