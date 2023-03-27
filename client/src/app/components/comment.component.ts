import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { MarvelService } from '../marvel.service';
import { Character, CommentObj } from '../model';

@Component({
  selector: 'app-comment',
  templateUrl: './comment.component.html',
  styleUrls: ['./comment.component.css']
})
export class CommentComponent implements OnInit, OnDestroy {

  // create a subscription to retrive the parameter id from the url
  id = ''
  params$!: Subscription

  // create a character object to store the subscribed object and project to html
  character: Character = {
    id: "",
    name: "",
    description: "",
    imageurl: ""
  }

  // create a subscription to listen to the event emitter from service
  sub$!: Subscription

  // create a form group for reactive form binding
  commentForm!: FormGroup

  constructor(private marvelSvc: MarvelService, private fb: FormBuilder, private activatedRoute: ActivatedRoute, private router: Router) {}

  ngOnInit(): void {
    console.info(">>> CommentComponent: ngOnInit called.")
    // retrive the route parameter id from the url, go to character xomponent.html to activate the route
    this.params$ = this.activatedRoute.params.subscribe(
      (params) => {
        this.id = params['id']
        console.info('>>> commentComponent: id: ', this.id)
      }
    )

    // subscription to service to get character object from api getCharacterById call
    this.sub$ = this.marvelSvc.onCharacterSearch.subscribe( // subscribe to service to listen to the data being passed out
      (character: Character | null) => { // this is the data that is being passed out to be received
        if (character) {
          console.log(">>> CommentComponent: Character object received:", character);
          this.character = character;
        }
      }
    )
    
    // create form control for form field binding and validation
    this.commentForm = this.fb.group({
      comment: this.fb.control('', Validators.required)
    })
  }

  doPostComment() {
    // get the comment from html field
    const commentObj: CommentObj = this.commentForm.value as CommentObj
    console.info('>>> commentForm: ngSubmit(): commentObj: ', commentObj)
    // post comment to mongodb
    this.marvelSvc.postCommentToMongo(this.id, commentObj)
      .then(result => {
        console.info('>>> doPostComment: in then, result: ', result)
      })
      .catch(error => {
        console.info('>>> doPostComment: in error')
        console.error('>>> doPostComment: error: ', error)
      })
    // reset the form
    this.commentForm.reset()
    this.backToCharacterView()
  }

  backToCharacterView() {
    // subscription to service to get character object from api getCharacterById call
    this.sub$ = this.marvelSvc.onCharacterSearch.subscribe( // subscribe to service to listen to the data being passed out
      (character: Character | null) => { // this is the data that is being passed out to be received
        if (character) {
          console.log(">>> CommentComponent: Character object received:", character);
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
