import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { BehaviorSubject, firstValueFrom, map, Observable, Subject, take } from "rxjs";
import { Character, CommentObj, InsertedComment } from "./model";

@Injectable()
export class MarvelService {

    // declare a subject to emit event out and for components to listen to this
    // onCharacterSearch = new Subject<Character>()
    // Create a BehaviorSubject to hold the character being searched
    onCharacterSearch: BehaviorSubject<Character | null> = new BehaviorSubject<Character | null>(null);
  
    // Make the BehaviorSubject observable to other components
    onCharacterSearch$ = this.onCharacterSearch.asObservable();

    /* the service is use to make http request, the data from the request can be pass 
    from the service to any of the component. just use the component that you want the data
    to be in to call the service. if search is the entry point, use navigate to get another component
    to call service so that the data can be passed into that component */

    constructor(private http: HttpClient) {}

    // http can return an observable<Character> or a promise<Character>
    // observable: do not need to use firstValueFrom, straight away return this.http.get
    // observable: int the ts file, subscribe to the observable
    // promise: use firstValueFrom and pipe the data if needed, if it is a nested json
    // promise: since this is already a jsonarray string, can cast it directly to a list of object
    // promise: in the ts file, just use .then => and allocate the result to the list
    getCharacters(searchTerm: string, limit: number, offset: number): Promise<Character[]> {
        const params = new HttpParams()
            .set("searchTerm", searchTerm)
            .set("limit", limit)
            .set("offset", offset)

        return firstValueFrom<Character[]>(
            this.http.get<Character[]>('/api/characters', { params }) // send http get request to springboot
        )
    }

    getCharacterById(id: string): Promise<Character> {
        return firstValueFrom<Character>(
            this.http.get<any>(`/api/character/${id}`) // send http get request to springboot
                .pipe(
                    take(1),
                    map(c => {
                        return {
                            id: c.id,
                            name: c.name,
                            description: c.description,
                            imageurl: c.imageurl,
                        } as Character
                    })
                )
        )
        .then((characterObject) => {
            console.log("Character object emitted:", characterObject);
            this.onCharacterSearch.next(characterObject) // send out the data to whichever component listening
            console.log("Character object emitted successfully!");
            return characterObject;
        })
    }

    postCommentToMongo(id: string, commentObj: CommentObj): Promise<Comment> {

        const headers = new HttpHeaders()
            .set('Content-Type', 'application/json')
            .set('Accept', 'application/json')

        return firstValueFrom(
            this.http.post<Comment>(`/api/character/${id}/comment`, commentObj, { headers: headers })
        )
    }

    getComments(id: string): Promise<InsertedComment[]> {
        const params = new HttpParams()
        .set("id", id)

        return firstValueFrom(
            this.http.get<InsertedComment[]>('/api/character/comments', { params })
        )
    }

    getCommentById(commentId: string): Promise<InsertedComment[]> {
        const params = new HttpParams()
        .set("commentId", commentId)

        return firstValueFrom(
            this.http.get<InsertedComment[]>('/api/character/comment', { params })
        )
    }

    updateCommentToMongo(commentId: string, commentObj: CommentObj): Promise<Comment> {

        const headers = new HttpHeaders()
            .set('Content-Type', 'application/json')    
            .set('Accept', 'application/json')
        return firstValueFrom(
            this.http.put<Comment>(`/api/character/editcomment/${commentId}`, commentObj, { headers: headers })
        )
    }
 
    deleteCommentById(id: string): Promise<any> {
        const params = new HttpParams()
        .set("id", id)

        return firstValueFrom(
            this.http.delete('/api/character/comments', { params })
        )
    }


}
