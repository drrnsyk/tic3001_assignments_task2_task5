import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { MarvelService } from '../marvel.service';
import { Character, CommentObj, InsertedComment } from '../model';

@Component({
  selector: 'app-editcomments',
  templateUrl: './editcomments.component.html',
  styleUrls: ['./editcomments.component.css']
})
export class EditcommentsComponent implements OnInit, OnDestroy {

  // create a subsctiption to retrieve the parameter id from the url
  commentId = ''
  params$!: Subscription

  // create a character object to store the subscribed object and project to html
  character: Character = {
    id: "",
    name: "",
    description: "",
    imageurl: ""
  }

  insertedComments: InsertedComment[] = []
  comment = ''

   // create a subscription to listen to the event emitter from service
   sub$!: Subscription

   // create a form group for reactive form binding
   editCommentForm!: FormGroup
 
   constructor(private marvelSvc: MarvelService, private fb: FormBuilder, private activatedRoute: ActivatedRoute, private router: Router) {}
 
   ngOnInit(): void {
     console.info(">>> editCommentComponent: ngOnInit called.")
     // retrive the route parameter id from the url, go to character component.html to activate the route
     this.params$ = this.activatedRoute.params.subscribe(
       (params) => {
         this.commentId = params['commentId']
         console.info('>>> editCommentComponent: commentId: ', this.commentId)
       }
     )
 
     // subscription to service to get character object from api getCharacterById call
     this.sub$ = this.marvelSvc.onCharacterSearch.subscribe( // subscribe to service to listen to the data being passed out
       (character: Character | null) => { // this is the data that is being passed out to be received
         if (character) {
           console.log(">>> editCommentComponent: Character object received:", character);
           this.character = character;
         }
       }
     )

     // get the comment to be edited by the comment id
     this.marvelSvc.getCommentById(this.commentId)
     .then(result => {
      console.info('>>> editCommentComponent: in then, comment, result: ', result)
      this.insertedComments = result
      this.comment = this.insertedComments[0].comment
      console.info('>>> editCommentComponent: in then, comment, result: ', this.comment)
      })
      .catch(error => {
        console.info('>>> editCommentComponent: in error')
        console.error('>>> editCommentComponent: error: ', error)
      })
      
     // create form control for form field binding and validation
     this.editCommentForm = this.fb.group({
       comment: this.fb.control("", Validators.required)
     })
   }

   doUpdateComment() {
    // get the comment from html field
    const commentObj: CommentObj = this.editCommentForm.value as CommentObj
    console.info('>>> editCommentForm: ngSubmit(): commentObj: ', commentObj)
    // post comment to mongodb
    this.marvelSvc.updateCommentToMongo(this.commentId, commentObj)
      .then(result => {
        console.info('>>> doEditComment: in then, result: ', result)
      })
      .catch(error => {
        console.info('>>> doEditComment: in error')
        console.error('>>> doEditComment: error: ', error)
      })
    // reset the form
    this.editCommentForm.reset()
    this.backToCharacterView()
  }

   backToCharacterView() {
    // subscription to service to get character object from api getCharacterById call
    this.sub$ = this.marvelSvc.onCharacterSearch.subscribe( // subscribe to service to listen to the data being passed out
      (character: Character | null) => { // this is the data that is being passed out to be received
        if (character) {
          console.log(">>> editCommentComponent: Character object received:", character);
          this.character = character;
        }
      }
    )
    this.router.navigate(['/character', this.character.id])
  }

  ngOnDestroy(): void {
      this.sub$.unsubscribe() // remember to destroy and unsub
  }

  


}
