import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { MarvelService } from '../marvel.service';
import { Character, InsertedComment } from '../model';

@Component({
  selector: 'app-character',
  templateUrl: './character.component.html',
  styleUrls: ['./character.component.css']
})
export class CharacterComponent implements OnInit {

  id = ''
  params$!: Subscription
  character!: Character
  insertedComments: InsertedComment[] = []


  constructor(private activatedRoute: ActivatedRoute, private router: Router, private marvelSvc: MarvelService) {}

  // retrieving the route paramemeter id from the url, go to search component.html to activate the route
  ngOnInit(): void {
      this.params$ = this.activatedRoute.params.subscribe(
        (params)=>{
          this.id = params['id']
          console.info('>>> characterComponent: id: ', this.id)
          this.marvelSvc.getCharacterById(this.id)
            .then(result => {
              console.info('>>> characterComponent: in then, result: ', result)
              this.character = result
            })
            .catch(error => {
              console.info('>>> characterComponent: in error')
              console.error('>>> characterComponent: error: ', error)
            })
          
          // get comments from mongodb by id
          this.marvelSvc.getComments(this.id)
            .then(result => {
              console.info('>>> characterComponent: in then, comments, result: ', result)
              this.insertedComments = result
            })
            .catch(error => {
              console.info('>>> characterComponent: in error')
              console.error('>>> characterComponent: error: ', error)
            })
        }
      )
  }

  doEditComment(id: string) {
    this.router.navigate(['/character',  this.character.id, 'editcomment', id])
  }

  doDeleteComment (id: string) {
    this.marvelSvc.deleteCommentById(id)
    this.ngOnInit()
  }


}
