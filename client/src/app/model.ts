export interface Character {
    id: string
    name: string
    description: string
    imageurl: string
}

export interface CommentObj {
    comment: string
}

export interface InsertedComment {
    commentId: string
    comment: string
    postedDate: string
}